package org.apache.fineract.tasklist;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.core.service.ThreadLocalContextUtil;
import org.apache.fineract.organisation.tenant.TenantServerConnectionRepository;
import org.apache.fineract.tasklist.entity.FormData;
import org.apache.fineract.tasklist.entity.ZeebeTaskCandidateRole;
import org.apache.fineract.tasklist.entity.ZeebeTaskCandidateRoleId;
import org.apache.fineract.tasklist.entity.ZeebeTaskEntity;
import org.apache.fineract.tasklist.entity.ZeebeTaskSubmitter;
import org.apache.fineract.tasklist.entity.ZeebeTaskSubmitterId;
import org.apache.fineract.tasklist.repository.ZeebeTaskRepository;
import org.apache.fineract.utils.StringUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserTaskJobHandler implements JobHandler {

    private static Logger logger = LoggerFactory.getLogger(UserTaskJobHandler.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ZeebeTaskRepository zeebeTaskRepository;

    @Autowired
    private TenantServerConnectionRepository tenantServerConnectionRepository;

    @Override
    @JobWorker(timeout = 2592000000L, name = "human-tasklist", type = "io.camunda.zeebe:userTask", autoComplete = false)
    public void handle(JobClient client, ActivatedJob job) {

        try {
            ThreadLocalContextUtil.setTenant(this.tenantServerConnectionRepository.findOneBySchemaName("binx"));
            final ZeebeTaskEntity entity = new ZeebeTaskEntity();


            long taskId = job.getKey();
            entity.setId(taskId);
            entity.setTimestamp(Instant.now().toEpochMilli());
            entity.setVariables(job.getVariables());

            final Map<String, String> customHeaders = job.getCustomHeaders();
            final Map<String, Object> variables = job.getVariablesAsMap();

            final String name = customHeaders.getOrDefault("name", job.getElementId());
            entity.setName(name);

            final String description = customHeaders.getOrDefault("description", "");
            entity.setDescription(description);

            final String taskForm = customHeaders.get("taskForm");
            entity.setTaskForm(taskForm);

            final String zeebeFormData = customHeaders.get("formData");
            List<FormData> formDataList = new ArrayList<>();
            if (StringUtils.isNotEmpty(zeebeFormData)) {
                JSONArray zeebeFormDataJsonArray = new JSONArray(zeebeFormData);
                zeebeFormDataJsonArray.forEach(o -> {
                    JSONObject jsonObject = (JSONObject) o;
                    FormData formData = new FormData();
                    String variableName = jsonObject.getString("variable");
                    formData.setName(variableName);
                    formData.setDescription(jsonObject.getString("description"));
                    formData.setValue(variables.getOrDefault(variableName, null));
                    formData.setIndex(jsonObject.getInt("index"));
                    formDataList.add(formData);
                });
                try {
                    entity.setFormData(objectMapper.writeValueAsString(formDataList));
                } catch (JsonProcessingException e) {
                    logger.error("Could not map form data object to string for taskId:" + taskId, e);
                }
            }

            final String assignee = readAssignee(customHeaders, variables);
            entity.setAssignee(assignee);

            Set<ZeebeTaskCandidateRole> candidateRoles = getCandidateRoles(customHeaders, taskId);
            Set<ZeebeTaskSubmitter> submitters = getSubmitters(variables, taskId, name);
            entity.setCandidateRoles(candidateRoles);
            entity.setPreviousSubmitters(submitters);
            Object endToEndId = variables.getOrDefault("endToEndId", null);
            entity.setEndToEndId(endToEndId == null ? null : endToEndId.toString());

            zeebeTaskRepository.save(entity);

        } finally {
            ThreadLocalContextUtil.clear();
        }
    }

    private static String readAssignee(
            final Map<String, String> customHeaders, final Map<String, Object> variables) {
        return customHeaders.getOrDefault(
                "io.camunda.zeebe:assignee", (String) variables.get("assignee"));
    }

    private Set<ZeebeTaskCandidateRole> getCandidateRoles(
            final Map<String, String> customHeaders, Long taskId) {
        final String candidateRolesString = customHeaders.getOrDefault("io.camunda.zeebe:candidateGroups", null);

        return StringUtil.jsonArrayToStringList(candidateRolesString)
                .stream()
                .map(s -> new ZeebeTaskCandidateRole(new ZeebeTaskCandidateRoleId(taskId, s)))
                .collect(Collectors.toSet());
    }

    private Set<ZeebeTaskSubmitter> getSubmitters(Map<String, Object> variables, Long taskId, String taskName) {
        final Object submittersObject = variables.getOrDefault(StringUtil.submitterVariableName(taskName), null);
        String submittersString;
        if (submittersObject != null) {
            submittersString = submittersObject.toString();
            List<String> submitterNames = StringUtil.commaSeparatedStringToList(submittersString);
            if (submitterNames.isEmpty()) {
                return Collections.emptySet();
            } else {
                return submitterNames.stream().map(s -> new ZeebeTaskSubmitter(new ZeebeTaskSubmitterId(taskId, s))).collect(Collectors.toSet());
            }
        } else {
            return Collections.emptySet();
        }
    }
}

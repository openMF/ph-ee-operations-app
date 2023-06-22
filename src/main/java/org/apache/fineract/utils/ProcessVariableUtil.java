package org.apache.fineract.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.tasklist.entity.FormData;
import org.apache.fineract.tasklist.entity.ZeebeTaskCandidateRole;
import org.apache.fineract.tasklist.entity.ZeebeTaskCandidateRoleId;
import org.apache.fineract.tasklist.entity.ZeebeTaskSubmitter;
import org.apache.fineract.tasklist.entity.ZeebeTaskSubmitterId;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ProcessVariableUtil {

    private static final Logger logger = LoggerFactory.getLogger(ProcessVariableUtil.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String getTaskFormWithDefaultValueMappings(Map<String, String> customHeaders, Map<String, Object> variables) {
        final String taskForm = customHeaders.getOrDefault("taskForm", null);
        if (StringUtils.isEmpty(taskForm)) {
            return null;
        }
        JSONArray taskFormJsonArray = new JSONArray(taskForm);
        taskFormJsonArray.forEach(o -> {
            JSONObject taskFormJsonObject = (JSONObject) o;
            if (taskFormJsonObject.has("defaultValueMapping")) {
                Object defaultValueMapping = variables.getOrDefault(taskFormJsonObject.getString("defaultValueMapping"), null);
                if (defaultValueMapping != null) {
                    taskFormJsonObject.put("value", defaultValueMapping.toString());
                }
            }
        });
        return taskFormJsonArray.toString();
    }

    public static String getFormData(long taskId, Map<String, String> customHeaders, Map<String, Object> variables) {
        final String zeebeFormData = customHeaders.getOrDefault("formData", null);
        if (StringUtils.isEmpty(zeebeFormData)) {
            return null;
        }
        List<FormData> formDataList = new ArrayList<>();
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
            return objectMapper.writeValueAsString(formDataList);
        } catch (JsonProcessingException e) {
            logger.error("Could not map form data object to string for taskId:" + taskId, e);
            return null;
        }
    }

    public static String getAssignee(
            final Map<String, String> customHeaders, final Map<String, Object> variables) {
        return customHeaders.getOrDefault(
                "io.camunda.zeebe:assignee", (String) variables.get("assignee"));
    }

    public static Set<ZeebeTaskCandidateRole> getCandidateRoles(
            final Map<String, String> customHeaders, Long taskId) {
        final String candidateRolesString = customHeaders.getOrDefault("io.camunda.zeebe:candidateGroups", null);

        return StringUtil.jsonArrayToStringList(candidateRolesString)
                .stream()
                .map(s -> new ZeebeTaskCandidateRole(new ZeebeTaskCandidateRoleId(taskId, s)))
                .collect(Collectors.toSet());
    }

    public static Set<ZeebeTaskSubmitter> getSubmitters(Map<String, Object> variables, Long taskId, String taskName) {
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

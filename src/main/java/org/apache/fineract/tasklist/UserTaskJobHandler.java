package org.apache.fineract.tasklist;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.apache.fineract.core.service.ThreadLocalContextUtil;
import org.apache.fineract.core.tenants.TenantsService;
import org.apache.fineract.tasklist.entity.ZeebeTaskEntity;
import org.apache.fineract.tasklist.repository.ZeebeTaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

import static org.apache.fineract.utils.ProcessVariableUtil.*;

@Component
public class UserTaskJobHandler implements JobHandler {

    private static Logger logger = LoggerFactory.getLogger(UserTaskJobHandler.class);

    @Autowired
    private ZeebeTaskRepository zeebeTaskRepository;

    @Autowired
    private TenantsService tenantsService;


    @Override
    @JobWorker(timeout = 2592000000L, name = "zeebe-tasklist", type = "io.camunda.zeebe:userTask", autoComplete = false)
    public void handle(JobClient client, ActivatedJob job) {

        Long taskId = job.getKey();
        try {
            ThreadLocalContextUtil.setTenantDataSource(tenantsService.getAnyDataSource());  // TODO @Karesz use the right tenant connection here
            final ZeebeTaskEntity entity = new ZeebeTaskEntity();

            entity.setId(taskId);
            entity.setTimestamp(Instant.now().toEpochMilli());
            entity.setVariables(job.getVariables());

            final Map<String, String> customHeaders = job.getCustomHeaders();
            final Map<String, Object> variables = job.getVariablesAsMap();

            final String name = customHeaders.getOrDefault("name", job.getElementId());
            entity.setName(name);

            entity.setDescription(customHeaders.getOrDefault("description", null));
            entity.setTaskForm(getTaskFormWithDefaultValueMappings(customHeaders, variables));
            entity.setFormData(getFormData(taskId, customHeaders, variables));
            entity.setAssignee(getAssignee(customHeaders, variables));
            entity.setCandidateRoles(getCandidateRoles(customHeaders, taskId));
            entity.setPreviousSubmitters(getSubmitters(variables, taskId, name));

            final String businessKeyVariableName = customHeaders.getOrDefault("businessKey", null);
            entity.setBusinessKey(variables.getOrDefault(businessKeyVariableName, taskId).toString());

            zeebeTaskRepository.save(entity);

        } catch (Exception e) {
            logger.error(String.format("Error while saving human task with id %s", taskId), e);
            throw new RuntimeException(e);
        } finally {
            ThreadLocalContextUtil.clear();
        }
    }
}

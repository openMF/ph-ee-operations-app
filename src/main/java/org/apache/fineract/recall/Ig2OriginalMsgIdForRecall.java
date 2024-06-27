package org.apache.fineract.recall;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.apache.fineract.operations.Transfer;
import org.apache.fineract.operations.TransferRepository;
import org.apache.fineract.operations.Variable;
import org.apache.fineract.operations.VariableRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@Component
public class Ig2OriginalMsgIdForRecall implements JobHandler {

    private static final Logger logger = LoggerFactory.getLogger(Ig2OriginalMsgIdForRecall.class);

    private static final ObjectMapper mapper = JsonMapper.builder()
            .findAndAddModules()
            .configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .build();

    @Autowired
    private VariableRepository variableRepository;

    @Autowired
    private TransferRepository transferRepository;

    private record FileMetaData(String messageId,
                                String fileType,
                                String contentType,
                                String fileName,
                                String processType,
                                String messageDirection,
                                String fileDateTime) {
    }

    @Override
    @JobWorker(type = "getOriginalMsgIdForRecall", autoComplete = false)
    public void handle(JobClient client, ActivatedJob job) {
        logger.info("IG2 incoming recall called getOriginalMsgIdForRecall");

        try {
            final Map<String, Object> variables = job.getVariablesAsMap();
            logger.trace("variables: {}", variables);
            if (!variables.containsKey("transactionId")) {
                throw new RuntimeException("transactionId variable not found");
            }
            String internalCorrelationId = (String) variables.get("internalCorrelationId");

            logger.debug("searching for original incoming transfer where transactionId is {}", internalCorrelationId);
            Transfer incomingTransfer = transferRepository.findIcomingTransfersForRecall(internalCorrelationId);
            if (incomingTransfer == null) {
                throw new RuntimeException("transfer not found for internalCorrelationId " + internalCorrelationId);
            }
            Long workflowInstanceKey = incomingTransfer.getWorkflowInstanceKey();
            if (logger.isTraceEnabled()) {
                logger.trace("found original incoming transfer: {}", incomingTransfer);
            } else {
                logger.debug("found original incoming transfer with workflowInstanceKey {}", workflowInstanceKey);
            }

            logger.debug("searching for variable pacs008FileMetadata where workflowInstanceKey is {}", workflowInstanceKey);
            Optional<Variable> pacs008FileMetadata = variableRepository.findByWorkflowInstanceKeyAndVariableName("pacs008FileMetadata", workflowInstanceKey);
            if (pacs008FileMetadata.isEmpty()) {
                throw new RuntimeException("pacs008FileMetadata not found for internalCorrelationId " + internalCorrelationId);
            }

            String json = pacs008FileMetadata.get().getValue();
            FileMetaData fileMetaData;
            try {
                fileMetaData = mapper.readValue(json, FileMetaData.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("failed to parse json from pacs008FileMetadata '" + json + "'", e);
            }

            logger.debug("for the internalCorrelationId {} the originalMessageId found is {}", internalCorrelationId, fileMetaData.messageId());

            client.newCompleteCommand(job)
                    .variables(Map.of("originalMessageId", fileMetaData.messageId()))
                    .send()
                    .whenComplete((response, exception) -> {
                        if (exception != null) {
                            logger.error("failed to complete job {}: {}", job.getType(), exception.getMessage(), exception);
                        }
                        logger.debug("completed job {}", job.getType());
                    });

        } catch (Exception e) {
            if (e.getCause() != null) {
                logger.error("{} worker failed with: '{}' caused by '{}'", job.getType(), e.getMessage(), e.getCause().getMessage(), e);
            } else {
                logger.error("{} worker failed with: '{}'", job.getType(), e.getMessage(), e);
            }

            if (e instanceof NullPointerException || job.getRetries() <= 0) {
                client.newThrowErrorCommand(job)
                        .errorCode("Error_OriginalMsgIdForRecallNotFound")
                        .send()
                        .whenComplete((response, exception) -> {
                            if (exception != null) {
                                logger.error("failed to throw bpmn error in job {}: {}", job.getType(), exception.getMessage(), exception);
                                return;
                            }
                            logger.debug("thrown bpmn error in job {}", job.getType());
                        });

            } else {
                client.newFailCommand(job)
                        .retries(job.getRetries() - 1)
                        .retryBackoff(Duration.ofHours(1))
                        .send()
                        .whenComplete((response, exception) -> {
                            if (exception != null) {
                                logger.error("failed to fail job {}: {}", job.getType(), exception.getMessage(), exception);
                            }
                            logger.debug("failed job {}", job.getType());
                        });
            }
        }
    }
}
package org.apache.fineract.recall;

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

import java.util.Map;
import java.util.Optional;

@Component
public class Ig2OriginalMsgIdForRecall implements JobHandler {

    private static final Logger logger = LoggerFactory.getLogger(Ig2OriginalMsgIdForRecall.class);

    @Autowired
    private VariableRepository variableRepository;

    @Autowired
    private TransferRepository transferRepository;

    @Override
    @JobWorker(name = "getOriginalMsgIdForRecall", autoComplete = false)
    public void handle(JobClient client, ActivatedJob job) throws Exception {
        logger.info("IG2 incoming recall called getOriginalMsgIdForRecall");

        final Map<String, Object> variables = job.getVariablesAsMap();
        logger.trace("variables: {}", variables);
        if (!variables.containsKey("transactionId")) {
            throw new RuntimeException("transactionId variable not found");
        }
        String transactionId = (String) variables.get("transactionId");

        logger.debug("searching for original incoming transfer where transactionId is {}", transactionId);
        Transfer incomingTransfer = transferRepository.findFirstByTransactionIdAndDirection(transactionId, "INCOMING");
        if (incomingTransfer == null) {
            throw new RuntimeException("transfer not found for transactionId " + transactionId);
        }
        Long workflowInstanceKey = incomingTransfer.getWorkflowInstanceKey();
        if (logger.isTraceEnabled()) {
            logger.trace("found original incoming transfer: {}", incomingTransfer);
        } else {
            logger.debug("found original incoming transfer with workflowInstanceKey {}", workflowInstanceKey);
        }

        logger.debug("searching for variable originalMessageId where workflowInstanceKey is {}", workflowInstanceKey);
        Optional<Variable> originalMessageId = variableRepository.findByWorkflowInstanceKeyAndVariableName("originalMessageId", workflowInstanceKey);
        if (originalMessageId.isEmpty()) {
            throw new RuntimeException("originalMessageId not found for transactionId " + transactionId);
        }
        logger.debug("for the transactionId {} the originalMessageId found is {}", transactionId, originalMessageId.get());

        client.newCompleteCommand(job)
                .variables(Map.of("originalMessageId", originalMessageId.get()))
                .send()
                .join();
    }
}
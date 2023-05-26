package org.apache.fineract.core.service;

import io.camunda.zeebe.client.ZeebeClient;
import org.apache.fineract.operations.Transfer;
import org.apache.fineract.operations.Variable;
import org.apache.fineract.operations.VariableRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CamundaService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Value("${recall.bpmn-instant}")
    private String recallBpmnInstant;

    @Value("${recall.bpmn-batch}")
    private String recallBpmnBatch;

    @Autowired
    private ZeebeClient zeebeClient;

    @Autowired
    private VariableRepository variableRepository;


    public void startRecallFlow(String paymentScheme, Transfer transfer) {
        String pacs008 = getTransferVariable(transfer, "generatedPacs008");
        String iban = getTransferVariable(transfer, "iban");
        String tenantIdentifier = getTransferVariable(transfer, "tenantIdentifier");

        Map<String, String> variables = new HashMap<>();
        String bpmn;

        if ("HCT_INST".equalsIgnoreCase(paymentScheme)) {
            String pacs008TransactionIdentification = transfer.getTransactionId();

            bpmn = recallBpmnInstant;
            variables.put("originalPacs008", pacs008);
            variables.put("paymentScheme", "HCT_INST:RECALL");
            variables.put("iban", iban);
            variables.put("recallReason", "AB05");  // TODO validate / externalize
            variables.put("originalPacs008TransactionIdentification", pacs008TransactionIdentification);
            variables.put("creditorIban", "");
            variables.put("tenantIdentifier", tenantIdentifier);
            variables.put("transactionGroupId", "");

        } else {
            bpmn = recallBpmnBatch;
            variables.put("originalPacs008", pacs008);
            variables.put("paymentScheme", "IG2:RECALL");
            variables.put("iban", iban);
            variables.put("recallReason", "DUP"); // TODO validate / externalize
            variables.put("tenantIdentifier", tenantIdentifier);
        }

        logger.debug("starting BPMN {} for paymentScheme {} using variables: {}", bpmn, paymentScheme, variables);

        zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId(bpmn)
                .latestVersion()
                .variables(variables)
                .send()
                .join();
    }

    private String getTransferVariable(Transfer transfer, String variableName) {
        Optional<Variable> optional = variableRepository.findByWorkflowInstanceKeyAndVariableName(variableName, transfer.getWorkflowInstanceKey());
        return optional.map(Variable::getValue).orElseGet(() -> {
            logger.warn("variable {} not found for transfer {}", variableName, transfer.getWorkflowInstanceKey());
            return "";
        });
    }
}

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
        String iban = getTransferVariable(transfer, "iban");
        String tenantIdentifier = getTransferVariable(transfer, "tenantIdentifier");
        String transactionGroupId = getTransferVariable(transfer, "transactionGroupId");
        String debtorIban = getTransferVariable(transfer, "debtorIban");
        String transactionId = getTransferVariable(transfer, "transactionId");
        Map<String, String> variables = new HashMap<>();
        String bpmn;
        if ("HCT_INST".equalsIgnoreCase(paymentScheme)) {
            bpmn = recallBpmnInstant;
            variables.put("originalPacs008", getTransferVariable(transfer, "generatedPacs008"));
            variables.put("paymentScheme", "HCT_INST:RECALL");
            variables.put("recallAdditionalInformation", "");  // TODO take value from UI field
        } else {
            bpmn = recallBpmnBatch;
            variables.put("originalPacs008", getTransferVariable(transfer, "generatedPacs008Fragment"));
            variables.put("paymentScheme", "IG2:RECALL");
        }
        variables.put("iban", iban);
        variables.put("creditorIban", debtorIban);
        variables.put("transactionGroupId", transactionGroupId);
        variables.put("internalCorrelationId", transactionId);
        variables.put("tenantIdentifier", tenantIdentifier);
        variables.put("originalPacs008TransactionIdentification", transactionId);
        variables.put("recallReason", "TECH");  // TODO take value from UI field
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
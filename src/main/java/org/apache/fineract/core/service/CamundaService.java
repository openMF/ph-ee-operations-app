package org.apache.fineract.core.service;

import io.camunda.zeebe.client.ZeebeClient;
import org.apache.fineract.operations.Transfer;
import org.apache.fineract.operations.Variable;
import org.apache.fineract.operations.VariableRepository;
import org.json.JSONArray;
import org.json.JSONObject;
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


    public void startRecallFlow(String requestBody, String paymentScheme, Transfer transfer) {
        String iban = getTransferVariable(transfer, "iban");
        String tenantIdentifier = getTransferVariable(transfer, "tenantIdentifier");
        String transactionGroupId = getTransferVariable(transfer, "transactionGroupId");
        String debtorIban = getTransferVariable(transfer, "debtorIban");
        String transactionId = getTransferVariable(transfer, "transactionId");  // without prefix
        String internalCorrelationId = transfer.getTransactionId();// has BIC code as prefix
        Map<String, String> variables = new HashMap<>();
        String bpmn;
        String comment = null;
        String recallAdditionalInformation = "";
        try {
            JSONObject body = new JSONObject(requestBody);
            comment = body.optString("comment", null);
            recallAdditionalInformation =  body.optString("recallAdditionalInformation", "");
        } catch (Exception e) {
            logger.error("Could not parse recall request body {}, can not set comment on recall!", requestBody);
        }
        if ("HCT_INST".equalsIgnoreCase(paymentScheme)) {
            bpmn = recallBpmnInstant;
            variables.put("originalPacs008", getTransferVariable(transfer, "generatedPacs008"));
            variables.put("paymentScheme", "HCT_INST:RECALL");
            variables.put("recallAdditionalInformation", recallAdditionalInformation);
        } else {
            bpmn = recallBpmnBatch;
            variables.put("originalPacs008Fragment", getTransferVariable(transfer, "pacs008Fragment"));
            variables.put("paymentScheme", "IG2:RECALL");
            variables.put("originalFileMetadata", getTransferVariable(transfer, "fileMetadata"));
        }
        variables.put("partnerName", getTransferVariable(transfer, "partnerName"));
        variables.put("partnerIban", getTransferVariable(transfer, "partnerName"));
        variables.put("partnerBic", getTransferVariable(transfer, "partnerName"));
        variables.put("iban", iban);
        variables.put("creditorIban", debtorIban);
        variables.put("transactionGroupId", transactionGroupId);
        variables.put("internalCorrelationId", internalCorrelationId);
        variables.put("tenantIdentifier", tenantIdentifier);
        variables.put("originalPacs008TransactionIdentification", transactionId);
        variables.put("recallReason", comment);
        variables.put("transactionId", getTransferVariable(transfer, "transactionId"));

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

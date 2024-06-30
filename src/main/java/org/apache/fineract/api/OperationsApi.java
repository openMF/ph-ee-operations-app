package org.apache.fineract.api;


import com.baasflow.commons.events.EventLogLevel;
import com.baasflow.commons.events.EventService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.core.service.CamundaService;
import org.apache.fineract.core.service.TenantAwareHeaderFilter;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.operations.*;
import org.apache.fineract.operations.TransferDto;
import org.apache.fineract.operations.converter.TimestampToStringConverter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.fineract.api.OperationsApiConstants.TRANSACTION_DETAILS_RESOURCE_NAME;

@RestController
@SecurityRequirement(name = "auth")
@RequestMapping("/api/v1")
public class OperationsApi {

    private static final String RECALL_REASON = "recallReason";
    private static final String RECALLER_TYPE = "recallerType";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PlatformSecurityContext context;

    @Autowired
    private PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

    @Autowired
    private BusinessKeyRepository businessKeyRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private VariableRepository variableRepository;

    @Autowired
    private TransferRepository transferRepository;

    @Autowired
    private TransactionRequestRepository transactionRequestRepository;

    @Autowired
    private CamundaService camundaService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${channel-connector.url}")
    private String channelConnectorUrl;

    @Value("${channel-connector.transfer-path}")
    private String channelConnectorTransferPath;

    @Autowired
    private EventService eventService;

    @Autowired
    private ModelMapper modelMapper;


    @PostMapping("/transfer/{transactionId}/refund")
    public String refundTransfer(@RequestHeader("Platform-TenantId") String tenantId,
                                 @PathVariable("transactionId") String transactionId,
                                 @RequestBody String requestBody,
                                 HttpServletResponse response) {
        return eventService.auditedEvent(event -> event
                .setEvent("initiating refund for transfer")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setPayload(transactionId)
                .setPayloadType("string")
                .setTenantId(tenantId), event -> {

            Transfer existingIncomingTransfer = transferRepository.findFirstByTransactionIdAndDirection(transactionId, "INCOMING");
            if (existingIncomingTransfer == null || !TransferStatus.COMPLETED.equals(existingIncomingTransfer.getStatus())) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                JSONObject failResponse = new JSONObject();
                failResponse.put("response", "Requested incoming transfer does not exist or not yet completed!");
                return failResponse.toString();
            }

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Platform-TenantId", tenantId);
            httpHeaders.add("Content-Type", "application/json");
            // httpHeaders.add("Authorization", "Bearer token"); TODO auth needed?

            JSONObject channelRequest = prepareRefundRequest(requestBody, existingIncomingTransfer);

            ResponseEntity<String> channelResponse = restTemplate.exchange(channelConnectorUrl + channelConnectorTransferPath,
                    HttpMethod.POST,
                    new HttpEntity<String>(channelRequest.toString(), httpHeaders),
                    String.class);
            response.setStatus(channelResponse.getStatusCodeValue());
            return channelResponse.getBody();
        });
    }

    @PostMapping("/transfer/{transactionId}/recall")
    public CommandProcessingResult recallTransfer(@RequestHeader("Platform-TenantId") String tenantId,
                                                  @PathVariable("transactionId") String transactionId,
                                                  @RequestBody String requestBody) {
        return eventService.auditedEvent(event -> event
                .setEvent("initiating recall for transaction")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setPayload(transactionId)
                .setPayloadType("string")
                .setTenantId(tenantId), event -> {

            final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                    .recall(transactionId) //
                    .withJson(requestBody) //
                    .build();
            return this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        });
    }

    private JSONObject prepareRefundRequest(String requestBody, Transfer existingIncomingTransfer) {
        JSONObject channelRequest = new JSONObject();
        JSONObject payer = new JSONObject();
        JSONObject payerPartyIdInfo = new JSONObject();
        payerPartyIdInfo.put("partyIdType", existingIncomingTransfer.getPayeePartyIdType());
        payerPartyIdInfo.put("partyIdentifier", existingIncomingTransfer.getPayeePartyId());
        payer.put("partyIdInfo", payerPartyIdInfo);
        channelRequest.put("payer", payer);
        JSONObject payee = new JSONObject();
        JSONObject payeePartyIdInfo = new JSONObject();
        payeePartyIdInfo.put("partyIdType", existingIncomingTransfer.getPayerPartyIdType());
        payeePartyIdInfo.put("partyIdentifier", existingIncomingTransfer.getPayerPartyId());
        payee.put("partyIdInfo", payeePartyIdInfo);
        channelRequest.put("payee", payee);
        JSONObject amount = new JSONObject();
        amount.put("amount", existingIncomingTransfer.getAmount());
        amount.put("currency", existingIncomingTransfer.getCurrency());
        channelRequest.put("amount", amount);
        try {
            JSONObject body = new JSONObject(requestBody);
            String comment = body.optString("comment", null);
            if (comment != null) {
                JSONObject extensionList = new JSONObject();
                JSONArray extensions = new JSONArray();
                addExtension(extensions, "comment", comment);
                extensionList.put("extension", extensions);
                channelRequest.put("extensionList", extensionList);
            }
        } catch (Exception e) {
            logger.error("Could not parse refund request body {}, can not set comment on refund!", requestBody);
        }
        return channelRequest;
    }

    private void addExtension(JSONArray extensionList, String key, String value) {
        JSONObject extension = new JSONObject();
        extension.put("key", key);
        extension.put("value", value);
        extensionList.put(extension);
    }

    @GetMapping("/transfer/{workflowInstanceKey}")
    public TransferDetail transferDetails(@PathVariable Long workflowInstanceKey) {
        return eventService.auditedEvent(event -> event
                .setEvent("transferDetails invoked")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setPayload(Long.toString(workflowInstanceKey))
                .setPayloadType("string")
                .setTenantId(TenantAwareHeaderFilter.tenant.get()), event -> {
            this.context.jwt().validateHasReadPermission(TRANSACTION_DETAILS_RESOURCE_NAME);
            Transfer transfer = transferRepository.findFirstByWorkflowInstanceKey(workflowInstanceKey);
            List<Task> tasks = taskRepository.findByWorkflowInstanceKeyOrderByTimestamp(workflowInstanceKey);
            List<Variable> variables = variableRepository.findByWorkflowInstanceKeyOrderByName(workflowInstanceKey);
            variables.forEach(it -> {
                String value = StringEscapeUtils.unescapeJava(it.getValue());
                value = StringEscapeUtils.unescapeJson(value);
                value = value
                        .replaceAll("^\"", "")
                        .replaceAll("\"$", "")
                        .replaceAll("\n$", "")
                ;
                it.setValue(value);
            });
            if (transfer.getRecallDirection() != null) {
                setRecallerType(transfer, variables);
            }
            return new TransferDetail(
                    modelMapper.map(transfer, TransferDto.class),
                    tasks.stream().map(t -> {
                        modelMapper.addConverter(new TimestampToStringConverter());
                        return modelMapper.map(t, TaskDto.class);
                    }).collect(Collectors.toList()),
                    variables.stream().map(v -> modelMapper.map(v, VariableDto.class)).collect(Collectors.toList())
            );
        });
    }

    @GetMapping("/transactionRequest/{workflowInstanceKey}")
    public TransactionRequestDetail transactionRequestDetails(@PathVariable Long workflowInstanceKey) {
        return eventService.auditedEvent(event -> event
                .setEvent("transaction request details invoked")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setPayload(Long.toString(workflowInstanceKey))
                .setPayloadType("string")
                .setTenantId(TenantAwareHeaderFilter.tenant.get()), event -> {

            TransactionRequest transactionRequest = transactionRequestRepository.findFirstByWorkflowInstanceKey(workflowInstanceKey);
            List<Task> tasks = taskRepository.findByWorkflowInstanceKeyOrderByTimestamp(workflowInstanceKey);
            List<Variable> variables = variableRepository.findByWorkflowInstanceKeyOrderByName(workflowInstanceKey);
            return new TransactionRequestDetail(transactionRequest, tasks, variables);
        });
    }

    @GetMapping("/variables")
    public List<List<VariableDto>> variables(
            @RequestParam(value = "businessKey") String businessKey,
            @RequestParam(value = "businessKeyType") String businessKeyType
    ) {
        return eventService.auditedEvent(event -> event
                .setEvent("variables list loaded")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setPayload(businessKey)
                .setPayloadType("string")
                .setTenantId(TenantAwareHeaderFilter.tenant.get()), event -> loadTransfers(businessKey, businessKeyType).stream()
                        .map(transfer -> variableRepository.findByWorkflowInstanceKeyOrderByName(transfer.getWorkflowInstanceKey())
                                .stream().map(v -> modelMapper.map(v, VariableDto.class)).collect(Collectors.toList()))
                        .collect(Collectors.toList()));
    }

    @GetMapping("/tasks")
    public List<List<Task>> tasks(
            @RequestParam(value = "businessKey") String businessKey,
            @RequestParam(value = "businessKeyType") String businessKeyType
    ) {
        return eventService.auditedEvent(event -> event
                .setEvent("tasks details invoked")
                .setEventLogLevel(EventLogLevel.INFO)
                .setSourceModule("operations-app")
                .setPayload(businessKey)
                .setPayloadType("string")
                .setTenantId(TenantAwareHeaderFilter.tenant.get()), event -> {

            return loadTransfers(businessKey, businessKeyType).stream()
                    .map(transfer -> taskRepository.findByWorkflowInstanceKeyOrderByTimestamp(transfer.getWorkflowInstanceKey()))
                    .collect(Collectors.toList());
        });
    }

    private List<BusinessKey> loadTransfers(@RequestParam("businessKey") String businessKey, @RequestParam("businessKeyType") String businessKeyType) {
        List<BusinessKey> businessKeys = businessKeyRepository.findByBusinessKeyAndBusinessKeyType(businessKey, businessKeyType);
        logger.debug("loaded {} transfer(s) for business key {} of type {}", businessKeys.size(), businessKey, businessKeyType);
        return businessKeys;
    }

    private void setRecallerType(Transfer transfer, List<Variable> variables) {
        Optional<Variable> recallReasonOpt = variables.stream().filter(v -> RECALL_REASON.equals(v.getName())).findFirst();
        if (recallReasonOpt.isPresent()) {
            Variable recallReason = recallReasonOpt.get();
            Variable newVariable = new Variable(recallReason.getWorkflowInstanceKey(), RECALLER_TYPE, recallReason.getWorkflowKey(), recallReason.getTimestamp(),
                    null, RecallReason.getRecallerType(recallReason.getValue()), null, transfer);

            ListIterator<Variable> iterator = variables.listIterator();
            while (iterator.hasNext()) {
                Variable var = iterator.next();
                if (var.getName().compareTo(newVariable.getName()) > 0) {
                    iterator.previous();
                    iterator.add(newVariable);
                    return;
                }
            }
            variables.add(newVariable);
        }
    }
}

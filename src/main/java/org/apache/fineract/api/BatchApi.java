package org.apache.fineract.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.apache.fineract.config.PaymentModeConfiguration;
import org.apache.fineract.file.FileTransferService;
import org.apache.fineract.operations.*;
import org.apache.fineract.response.BatchAndSubBatchSummaryResponse;
import org.apache.fineract.response.SubBatchSummary;
import org.apache.fineract.service.BatchDbService;
import org.apache.fineract.service.BatchService;
import org.apache.fineract.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.apache.fineract.core.service.OperatorUtils.strip;

@Slf4j
@RestController
@SecurityRequirement(name = "auth")
@RequestMapping("/api/v1")
public class BatchApi {
    @Autowired
    private TransferRepository transferRepository;

    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private VariableRepository variableRepository;

    @Autowired
    @Qualifier("awsStorage")
    private FileTransferService fileTransferService;

    @Autowired
    private PaymentModeConfiguration paymentModeConfig;

    @Autowired
    private DateUtil dateUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BatchDbService batchDbService;

    @Value("${application.bucket-name}")
    private String bucketName;
    @Autowired
    private BatchService batchService;

    private Sort getSortObject(String sort) {
        Sort.Direction sortDirection;
        String sortedBy;
        if (sort.contains("+") && sort.split("\\+").length == 2) {
            sortDirection = Sort.Direction.ASC;
            sortedBy = sort.split("\\+")[1];
        } else if (sort.contains("-") && sort.split("-").length == 2) {
            sortDirection = Sort.Direction.DESC;
            sortedBy = sort.split("-")[1];
        } else {
            sortDirection = Sort.Direction.ASC;
            sortedBy = sort;
        }
        sortedBy = sortedBy.replace(" ", "");
        log.info("Sorting by: {} and Sorting direction: {}", sortedBy, sortDirection.name());
        return new Sort(sortDirection, sortedBy);
    }

    @GetMapping("/batches")
    public BatchPaginatedResponse getBatch(@RequestParam(value = "offset", required = false, defaultValue = "0")
                                           Integer offset,
                                           @RequestParam(value = "limit", required = false, defaultValue = "10")
                                           Integer limit,
                                           @RequestParam(value = "sort", required = false,
                                                   defaultValue = "-startedAt") String sort,
                                           @RequestParam(value = "dateFrom", required = false) String startFrom,
                                           @RequestParam(value = "dateTo", required = false) String startTo,
                                           @RequestParam(value = "registeringInstitutionId", required = false,
                                                   defaultValue = "%") String registeringInstituteId,
                                           @RequestParam(value = "payerFsp", required = false, defaultValue = "%")
                                               String payerFsp,
                                           @RequestParam(value = "batchId", required = false, defaultValue = "%")
                                               String batchId,
                                           HttpServletResponse httpServletResponse) {
        log.info("Registering Id: {}, PayerFsp: {}, batchId: {}", registeringInstituteId, payerFsp, batchId);
        Sort sortObject = getSortObject(sort);
        int page = Math.floorDiv(offset, limit);
        PageRequest pager = PageRequest.of(page, limit, sortObject);

        if (startFrom != null) {
            startFrom = dateUtil.getUTCFormat(startFrom);
        }
        if (startTo != null) {
            startTo = dateUtil.getUTCFormat(startTo);
        }
        try {
            BatchPaginatedResponse batchPaginatedResponse;

            if (startFrom != null && startTo != null) {
                batchPaginatedResponse = batchDbService.getBatch(startFrom, startTo, registeringInstituteId, payerFsp, batchId, pager);
            } else if (startFrom != null) {
                batchPaginatedResponse = batchDbService.getBatchDateFrom(startFrom, registeringInstituteId, payerFsp, batchId, pager);
            } else if (startTo != null) {
                batchPaginatedResponse = batchDbService.getBatchDateTo(startTo, registeringInstituteId, payerFsp, batchId, pager);
            } else {
                batchPaginatedResponse = batchDbService.getBatch(registeringInstituteId, payerFsp, batchId, pager);
            }
            httpServletResponse.setStatus(200);
            return batchPaginatedResponse;
        } catch (Exception e) {
            e.printStackTrace();
            httpServletResponse.setStatus(400);
            return null;
        }
    }

    @GetMapping("/batch")
    public ResponseEntity<Object> batchDetails(@RequestParam(value = "batchId", required = false) String batchId,
                                               @RequestParam(value = "requestId", required = false) String requestId) {
        Batch batch = batchRepository.findByBatchId(batchId);

        if (batch == null) {
            String errorMessage = "Batch corresponding to batchId: " + batchId + " does not exist.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }
        return ResponseEntity.ok(generateBatchSummaryResponse(batch));
    }

    @GetMapping("/batch/{batchId}")
    public ResponseEntity<Object> batchAggregation(@PathVariable(value = "batchId") String batchId,
                                     @RequestParam(value = "requestId", required = false) String requestId,
                                     @RequestParam(value = "command", required = false, defaultValue = "aggregate") String command) {
        Batch batch = batchRepository.findByBatchId(batchId);

        if (batch == null) {
            String errorMessage = "Batch corresponding to batchId: " + batchId + " does not exist.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }
        return ResponseEntity.ok(generateDetails(batch));
    }

    @GetMapping("/batch/detail")
    public Page<Transfer> batchDetails(HttpServletResponse httpServletResponse,
                                       @RequestParam(value = "batchId") String batchId,
                                       @RequestParam(value = "status", defaultValue = "ALL") String status,
                                       @RequestParam(value = "pageNo", defaultValue = "0") Integer pageNo,
                                       @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                       @RequestParam(value = "command", required = false, defaultValue = "json") String command) {

        if (command.equalsIgnoreCase("download")) {
            Batch batch = batchRepository.findByBatchId(batchId);
            if (batch != null && batch.getResult_file() != null) {
                httpServletResponse.setHeader("Location", batch.getResult_file());
                httpServletResponse.setStatus(302);
            } else {
                httpServletResponse.setStatus(404);
            }
            return null;
        }

        Page<Transfer> transfers;

        if (status.equalsIgnoreCase(TransferStatus.COMPLETED.toString()) ||
                status.equalsIgnoreCase(TransferStatus.IN_PROGRESS.toString()) ||
                status.equalsIgnoreCase(TransferStatus.FAILED.toString())) {
            transfers = transferRepository.findAllByBatchIdAndStatus(batchId, status.toUpperCase(), new PageRequest(pageNo, pageSize));
        } else {
            transfers = transferRepository.findAllByBatchId(batchId, new PageRequest(pageNo, pageSize));
        }

        return transfers;
    }

    @GetMapping("/batch/transactions")
    public HashMap<String, String> batchTransactionDetails(@RequestParam String batchId) {
        Batch batch = batchRepository.findByBatchId(batchId);
        if (batch != null) {
            List<Transfer> transfers = transferRepository.findAllByBatchId(batch.getBatchId());
            HashMap<String, String> status = new HashMap<>();
            for (Transfer transfer : transfers) {
                status.put(transfer.getTransactionId(), transfer.getStatus().name());
            }
            return status;
        } else {
            return null;
        }
    }
    @GetMapping("/batches/{batchId}")
    public <T>ResponseEntity<T> getBatchAndSubBatchSummary(@PathVariable String batchId,
                                                                                      @RequestHeader(name = "X-Correlation-ID") String clientCorrelationId,
                                                                                      @RequestParam(value = "offset", required = false, defaultValue = "0")
                                                                                          Integer offset,
                                                                                      @RequestParam(value = "limit", required = false, defaultValue = "10")
                                                                                          Integer limit,
                                                                                      @RequestParam(value = "associations", required = false)
                                                                                          String associations,
                                                                                      @RequestParam(value = "orderBy", required = false, defaultValue = "instructionId")
                                                                                          String orderBy,
                                                                                      @RequestParam(value = "sortBy", required = false, defaultValue = "asc")
                                                                                          String sortBy){

        if (associations!=null && associations.equals("all")) {
            PaymentBatchDetail response = batchService.getPaymentBathDetail(batchId, clientCorrelationId, offset, limit, orderBy, sortBy);
            if (ObjectUtils.isEmpty(response)) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return (ResponseEntity<T>) new ResponseEntity<>(response, HttpStatus.OK);

        } else {
            BatchAndSubBatchSummaryResponse response = batchService.getBatchAndSubBatchSummary(batchId, clientCorrelationId);

            if (ObjectUtils.isEmpty(response)) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return (ResponseEntity<T>) new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
    @GetMapping("/batches/{batchId}/subBatches/{subBatchId}")
    public <T>ResponseEntity<T> getSubBatchPaymentDetail(@PathVariable String batchId, @PathVariable String subBatchId,
                                                           @RequestHeader(name = "X-Correlation-ID") String clientCorrelationId,
                                                           @RequestParam(value = "offset", required = false, defaultValue = "0")
                                                           Integer offset,
                                                           @RequestParam(value = "limit", required = false, defaultValue = "10")
                                                           Integer limit,
                                                         @RequestParam(value = "orderBy", required = false, defaultValue = "instructionId")
                                                             String orderBy,
                                                         @RequestParam(value = "sortBy", required = false, defaultValue = "asc")
                                                             String sortBy){

        SubBatchSummary response = batchService.getPaymentSubBatchDetail(batchId, subBatchId, clientCorrelationId, offset, limit, orderBy, sortBy);

        if (ObjectUtils.isEmpty(response)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return (ResponseEntity<T>) new ResponseEntity<>(response, HttpStatus.OK);
    }
    private void saveBatch(Batch batch,Long completed,Long ongoing,Long failed,
                           Long totalTransfers,Long totalAmount,Long totalCompletedAmount,
                           Long totalOngoingAmount,Long totalFailedAmount){
        batch.setCompleted(completed);
        batch.setFailed(failed);
        batch.setResultGeneratedAt(new Date());
        batch.setOngoing(ongoing);
        batch.setTotalTransactions(totalTransfers);
        batch.setTotalAmount(totalAmount);
        batch.setCompletedAmount(totalCompletedAmount);
        batch.setOngoingAmount(totalOngoingAmount);
        batch.setFailedAmount(totalFailedAmount);
        batchRepository.save(batch);
    }
    private BatchDTO getBatchSummary(Batch batch,String modes) {
        Double batchCompletedPercent = 0.0;
        Double batchFailedPercent = 0.0;
        if (batch.getCompleted() != null){
            batchCompletedPercent = (double) batch.getCompleted() / batch.getTotalTransactions() * 100;
        }
        if(batch.getFailed() != null){
            batchFailedPercent = (double) batch.getFailed() / batch.getTotalTransactions() * 100;
        }

        BatchDTO response = new BatchDTO(batch.getBatchId(),
                batch.getRequestId(), batch.getTotalTransactions(), batch.getOngoing(),
                batch.getFailed(), batch.getCompleted(),BigDecimal.valueOf(batch.getTotalAmount()),BigDecimal.valueOf(batch.getCompletedAmount()),
                BigDecimal.valueOf(batch.getOngoingAmount()), BigDecimal.valueOf(batch.getFailedAmount()), batch.getResult_file(), batch.getNote(),
                batchFailedPercent.toString(),batchCompletedPercent.toString(), batch.getRegisteringInstitutionId(),
                batch.getPayerFsp(), batch.getCorrelationId());

        response.setCreatedAt("" + batch.getStartedAt());
        response.setModes(modes);
        response.setPurpose("Unknown purpose");
        System.out.println("Batch details generated for batchId: " + response.getSuccessPercentage());

        if (batch.getCompleted().longValue() == batch.getTotalTransactions().longValue()) {
            response.setStatus("COMPLETED");
        } else if (batch.getOngoing() != 0 && batch.getCompletedAt() == null) {
            response.setStatus("Pending");
        } else if (batch.getFailed().longValue() == batch.getFailed().longValue()) {
            response.setStatus("Failed");
        } else {
            response.setStatus("UNKNOWN");
        }

        return response;
    }
    private void evaluateBatchSummary(Batch batch) {
        Long completed = 0L;
        Long failed = 0L;
        Long total = 0L;
        Long ongoing = 0L;
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal completedAmount = BigDecimal.ZERO;
        BigDecimal ongoingAmount = BigDecimal.ZERO;
        BigDecimal failedAmount = BigDecimal.ZERO;
        List<Transfer> transfers = null;
        if(batch.getSubBatchId()!=null){
            transfers = transferRepository.findAllByBatchId(batch.getSubBatchId());
        }else{
            transfers = transferRepository.findAllByBatchId(batch.getBatchId());
        }

            for (Transfer transfer : transfers) {
                Optional<Variable> variable = variableRepository.findByWorkflowInstanceKeyAndVariableName("paymentMode",
                        transfer.getWorkflowInstanceKey());
                if (variable.isPresent()) {
                    // this will prevent 2x count of variables by eliminating data from transfers table
                    if (paymentModeConfig.getByMode(strip(variable.get().getValue()))
                            .getType().equalsIgnoreCase("BATCH")) {
                        continue;
                    }
                }
                total++;
                BigDecimal amount = transfer.getAmount();
                totalAmount = totalAmount.add(amount);
                if (transfer.getStatus().equals(TransferStatus.COMPLETED)) {
                    completed++;
                    completedAmount = completedAmount.add(amount);
                } else if (transfer.getStatus().equals(TransferStatus.FAILED)) {
                    failed++;
                    failedAmount = failedAmount.add(amount);
                } else if (transfer.getStatus().equals(TransferStatus.IN_PROGRESS)) {
                    if (transfer.getCompletedAt() == null || transfer.getCompletedAt().toString().isEmpty()) {
                        ongoing++;
                        ongoingAmount = ongoingAmount.add(amount);
                    } else {
                        completed++;
                        completedAmount = completedAmount.add(amount);
                    }
                }
            }
        if (batch.getResult_file() == null || (batch.getResult_file() != null && batch.getResult_file().isEmpty())) {
            batch.setResult_file(createDetailsFile(transfers));
        }
        saveBatch(batch, completed, ongoing, failed, total, totalAmount.longValue(), completedAmount.longValue(), ongoingAmount.longValue(), failedAmount.longValue());
    }
    private Batch getParentBatchSummary(List<Batch> batches){
        StringBuilder modes = new StringBuilder();

        Long subBatchFailed = 0L;
        Long subBatchCompleted = 0L;
        Long subBatchOngoing = 0L;
        Long subBatchTotal = 0L;

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal completedAmount = BigDecimal.ZERO;
        BigDecimal ongoingAmount = BigDecimal.ZERO;
        BigDecimal failedAmount = BigDecimal.ZERO;
        Batch parentBatch = null;

        for (Batch bt : batches) {
            if (bt.getPaymentMode() != null && !modes.toString().contains(bt.getPaymentMode())) {
                if (!modes.toString().equals("")) {
                    modes.append(",");
                }
                modes.append(bt.getPaymentMode());
            }
            if (bt.getSubBatchId() == null || bt.getSubBatchId().isEmpty()) {
                parentBatch = bt;
                continue;
            }
            if (bt.getFailed() != null) {
                subBatchFailed += bt.getFailed();
                failedAmount = failedAmount.add(BigDecimal.valueOf(bt.getFailedAmount()));
            }
            if (bt.getCompleted() != null) {
                subBatchCompleted += bt.getCompleted();
                completedAmount = completedAmount.add(BigDecimal.valueOf(bt.getCompletedAmount()));
            }
            if (bt.getOngoing() != null) {
                subBatchOngoing += bt.getOngoing();
                ongoingAmount = ongoingAmount.add(BigDecimal.valueOf(bt.getOngoingAmount()));
            }
            if (bt.getTotalTransactions() != null) {
                subBatchTotal += bt.getTotalTransactions();
                totalAmount = totalAmount.add(BigDecimal.valueOf(bt.getTotalAmount()));
            }
        }
        saveBatch(parentBatch,subBatchCompleted,subBatchOngoing,subBatchFailed,subBatchTotal,
                totalAmount.longValue(),completedAmount.longValue(),ongoingAmount.longValue(),failedAmount.longValue());
        return  parentBatch;
    }
    private BatchDTO generateDetails(Batch batch){

        StringBuilder modes = new StringBuilder();
        List<Batch> batches = batchRepository.findAllByBatchId(batch.getBatchId());
        for (Batch subBatch:batches){
            if (subBatch.getPaymentMode() != null && !modes.toString().contains(subBatch.getPaymentMode())) {
                if (!modes.toString().equals("")) {
                    modes.append(",");
                }
                modes.append(subBatch.getPaymentMode());
            }
            evaluateBatchSummary(subBatch);
        }
        Batch parentBatchSummary = null;
        if(batches.size()==1){
            parentBatchSummary = batch;
        }else{
            parentBatchSummary = getParentBatchSummary(batches);
        }


        return getBatchSummary(parentBatchSummary, modes.toString());
    }

    private String createDetailsFile(List<Transfer> transfers) {
        String CSV_SEPARATOR = ",";
        File tempFile = new File(System.currentTimeMillis() + "_response.csv");
        try (
                FileWriter writer = new FileWriter(tempFile.getName());
                BufferedWriter bw = new BufferedWriter(writer)) {
            for (Transfer transfer : transfers) {
                StringBuffer oneLine = new StringBuffer();
                oneLine.append(transfer.getTransactionId());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(transfer.getStatus().toString());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(transfer.getPayeeDfspId());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(transfer.getPayeePartyId());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(transfer.getPayerDfspId());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(transfer.getPayerPartyId());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(transfer.getAmount().toString());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(transfer.getCurrency());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(transfer.getErrorInformation());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(transfer.getStartedAt().toString());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(transfer.getCompletedAt().toString());
                oneLine.append(CSV_SEPARATOR);
                bw.write(oneLine.toString());
                bw.newLine();
            }
            bw.flush();
            return fileTransferService.uploadFile(tempFile, bucketName);
        } catch (Exception e) {
            System.err.format("Exception: %s%n", e);
        }
        return null;
    }

    private BatchDTO generateBatchSummaryResponse(Batch batch) {
        double batchFailedPercent = 0;
        double batchCompletedPercent = 0;

        if(batch.getTotalTransactions() != null){
            batchFailedPercent = ((double) batch.getFailed()) / batch.getTotalTransactions() * 100;
            batchCompletedPercent = ((double) batch.getCompleted()) / batch.getTotalTransactions() * 100;
        }

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setRoundingMode(RoundingMode.FLOOR);

        Optional<Long> totalAmount = Optional.ofNullable(batch.getTotalAmount());
        Optional<Long> completedAmount = Optional.ofNullable(batch.getCompletedAmount());
        Optional<Long> ongoingAmount = Optional.ofNullable(batch.getOngoingAmount());
        Optional<Long> failedAmount = Optional.ofNullable(batch.getFailedAmount());

        Long nullValue = 0L;

        BatchDTO batchDTO = new BatchDTO(batch.getBatchId(),
                batch.getRequestId(), batch.getTotalTransactions(), batch.getOngoing(),
                batch.getFailed(), batch.getCompleted(),
                BigDecimal.valueOf(totalAmount.orElse(nullValue)),
                BigDecimal.valueOf(completedAmount.orElse(nullValue)),
                BigDecimal.valueOf(ongoingAmount.orElse(nullValue)),
                BigDecimal.valueOf(failedAmount.orElse(nullValue)),
                batch.getResult_file(), batch.getNote(),
                decimalFormat.format(batchFailedPercent), decimalFormat.format(batchCompletedPercent),
                batch.getRegisteringInstitutionId(), batch.getPayerFsp(), batch.getCorrelationId());

        if (batch.getTotalTransactions() != null &&
                batch.getCompleted() != null &&
                batch.getTotalTransactions().longValue() == batch.getCompleted().longValue()) {
            batchDTO.setStatus("COMPLETED");
        } else if (batch.getOngoing() != null && batch.getOngoing() != 0 && batch.getCompletedAt() == null) {
            batchDTO.setStatus("Pending");
        } else {
            batchDTO.setStatus("UNKNOWN");
        }

        return batchDTO;
    }

}

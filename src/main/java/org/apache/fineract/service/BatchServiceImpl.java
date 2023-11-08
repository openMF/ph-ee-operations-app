package org.apache.fineract.service;

import org.apache.fineract.operations.Batch;
import org.apache.fineract.operations.BatchRepository;
import org.apache.fineract.operations.Transfer;
import org.apache.fineract.operations.TransferRepository;
import org.apache.fineract.response.BatchAndSubBatchSummaryResponse;
import org.apache.fineract.response.SubBatchSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BatchServiceImpl implements BatchService{
    
    @Autowired
    private BatchRepository batchRepository;
    @Autowired
    private TransferRepository transferRepository;
    private static long subBatchAmount;
    private static long subBatchCount;


    @Override
    public BatchAndSubBatchSummaryResponse getBatchAndSubBatchSummary(String batchId, String clientCorrelationId) {

        List<Batch> batchAndSubBatches = batchRepository.findAllByBatchId(batchId);

        if(CollectionUtils.isEmpty(batchAndSubBatches)){
            return null;
        }

        BatchAndSubBatchSummaryResponse response = new BatchAndSubBatchSummaryResponse();
        subBatchCount=0;
        subBatchAmount=0;
        Long totalSubBatch = 0L;

        for(Batch batch : batchAndSubBatches){
            if(StringUtils.isEmpty(batch.getSubBatchId())){
                updateResponseWithBatchInfo(batch, response);
            }
            else{
                SubBatchSummary subBatchSummary = updateResponseWithSubBatchInfo(batch, response);
                totalSubBatch++;
                response.getSubBatchSummaryList().add(subBatchSummary);
            }
        }
        response.setApprovedTransactionCount(subBatchCount);
        response.setApprovedAmount(subBatchAmount);
        response.setTotalSubBatches(totalSubBatch);

        return response;
    }

    private SubBatchSummary updateResponseWithSubBatchInfo(Batch batch, BatchAndSubBatchSummaryResponse response) {
        double batchFailedPercent = 0;
        double batchSuccessPercent = 0;

        if (batch != null) {
            if (CollectionUtils.isEmpty(response.getSubBatchSummaryList())) {
                response.setSubBatchSummaryList(new ArrayList<>());
            }

            if (batch.getTotalTransactions() != null) {
                batchFailedPercent = ((double) batch.getFailed()) / batch.getTotalTransactions() * 100;
                batchSuccessPercent = ((double) batch.getCompleted()) / batch.getTotalTransactions() * 100;
            }

            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            decimalFormat.setRoundingMode(RoundingMode.FLOOR);

            SubBatchSummary subBatchSummary = new SubBatchSummary();
            subBatchSummary.setBatchId(batch.getBatchId());
            subBatchSummary.setSubBatchId(batch.getSubBatchId());
            subBatchSummary.setRequestId(batch.getRequestId());

            subBatchSummary.setTotal(batch.getTotalTransactions() != null ? batch.getTotalTransactions() : 0);
            subBatchSummary.setTotalAmount(BigDecimal.valueOf(batch.getTotalAmount() != null ? batch.getTotalAmount() : 0));
            subBatchSummary.setOngoing(batch.getOngoing() != null ? batch.getOngoing() : 0);
            subBatchSummary.setPendingAmount(BigDecimal.valueOf(batch.getOngoingAmount() != null ? batch.getOngoingAmount() : 0));
            subBatchSummary.setSuccessful(batch.getCompleted() != null ? batch.getCompleted() : 0);
            subBatchSummary.setSuccessfulAmount(BigDecimal.valueOf(batch.getCompletedAmount() != null ? batch.getCompletedAmount() : 0));
            subBatchSummary.setFailed(batch.getFailed() != null ? batch.getFailed() : 0);
            subBatchSummary.setFailedAmount(BigDecimal.valueOf(batch.getFailedAmount() != null ? batch.getFailedAmount() : 0));
            subBatchSummary.setFile(batch.getRequestFile());
            subBatchSummary.setNotes(batch.getNote());
            subBatchSummary.setCreatedAt(batch.getStartedAt() != null ? batch.getStartedAt().toString() : null);

            subBatchSummary.setModes(batch.getPaymentMode());
            subBatchSummary.setPurpose(null);
            subBatchSummary.setSuccessPercentage(decimalFormat.format(batchSuccessPercent));
            subBatchSummary.setFailedPercentage(decimalFormat.format(batchFailedPercent));
            subBatchSummary.setApprovedAmount(batch.getApprovedAmount());
            subBatchSummary.setApprovedTransactionCount(batch.getApprovedCount());
            subBatchSummary.setPayerFsp(batch.getPayerFsp());
            subBatchAmount += batch.getApprovedAmount() != null ? batch.getApprovedAmount() : 0;
            subBatchCount += batch.getApprovedCount() != null ? batch.getApprovedCount() : 0;
            List<Transfer> transferList =  transferRepository.findAllBySubBatchId(batch.getSubBatchId());
            Set<String> payeeFspSet = new HashSet<>();
            for (Transfer transfer : transferList) {
                payeeFspSet.add(transfer.getPayeeDfspId());
            }
            subBatchSummary.setPayeeFspSet(payeeFspSet);


            return subBatchSummary;
        } else {
            return null; // Return null if batch is null
        }
    }
    private void updateResponseWithBatchInfo(Batch batch, BatchAndSubBatchSummaryResponse response) {
        double batchFailedPercent = 0;
        double batchSuccessPercent = 0;

        if (batch != null) {
            if (batch.getTotalTransactions() != null) {
                batchFailedPercent = ((double) batch.getFailed()) / batch.getTotalTransactions() * 100;
                batchSuccessPercent = ((double) batch.getCompleted()) / batch.getTotalTransactions() * 100;
            }

            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            decimalFormat.setRoundingMode(RoundingMode.FLOOR);

            response.setBatchId(batch.getBatchId());
            response.setRequestId(batch.getRequestId());

            response.setTotal(batch.getTotalTransactions() != null ? batch.getTotalTransactions() : 0);
            response.setTotalAmount(BigDecimal.valueOf(batch.getTotalAmount() != null ? batch.getTotalAmount() : 0));
            response.setOngoing(batch.getOngoing() != null ? batch.getOngoing() : 0);
            response.setPendingAmount(BigDecimal.valueOf(batch.getOngoingAmount() != null ? batch.getOngoingAmount() :0));
            response.setSuccessful(batch.getCompleted() != null ? batch.getCompleted() : 0);
            response.setSuccessfulAmount(BigDecimal.valueOf(batch.getCompletedAmount() !=null ? batch.getCompletedAmount(): 0));
            response.setFailed(batch.getFailed() != null ? batch.getFailed() : 0);
            response.setFailedAmount(BigDecimal.valueOf(batch.getFailedAmount()!= null ? batch.getFailedAmount() : 0));
            response.setFile(batch.getResult_file());
            response.setNotes(batch.getNote());

            response.setCreatedAt(batch.getStartedAt() != null ? batch.getStartedAt().toString() : null);

            response.setModes(batch.getPaymentMode());
            response.setPurpose(null);
            response.setSuccessPercentage(decimalFormat.format(batchSuccessPercent));
            response.setFailedPercentage(decimalFormat.format(batchFailedPercent));
            response.setApprovedTransactionCount(batch.getApprovedCount() != null ? batch.getApprovedCount() : 0);
            response.setApprovedAmount(batch.getApprovedAmount()!= null ? batch.getApprovedAmount() : 0);
            response.setPayerFsp(batch.getPayerFsp());
            response.setGeneratedAt(LocalDateTime.now().toString());
            response.setTotalInstructionCount(transferRepository.countAllByBatchId(batch.getBatchId()));


        }
    }
}

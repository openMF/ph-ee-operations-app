package org.apache.fineract.service;

import org.apache.fineract.operations.Batch;
import org.apache.fineract.operations.BatchRepository;
import org.apache.fineract.response.BatchAndSubBatchSummaryResponse;
import org.apache.fineract.response.SubBatchSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class BatchServiceImpl implements BatchService{
    
    @Autowired
    private BatchRepository batchRepository;

    @Override
    public BatchAndSubBatchSummaryResponse getBatchAndSubBatchSummary(String batchId, String clientCorrelationId) {

        List<Batch> batchAndSubBatches = batchRepository.findAllByBatchId(batchId);

        if(CollectionUtils.isEmpty(batchAndSubBatches)){
            return null;
        }

        BatchAndSubBatchSummaryResponse response = new BatchAndSubBatchSummaryResponse();

        for(Batch batch : batchAndSubBatches){
            if(StringUtils.isEmpty(batch.getSubBatchId())){
                updateResponseWithBatchInfo(batch, response);
            }
            else{
                SubBatchSummary subBatchSummary = updateResponseWithSubBatchInfo(batch, response);
                response.getSubBatchSummaryList().add(subBatchSummary);
            }
        }

        return response;
    }

    private SubBatchSummary updateResponseWithSubBatchInfo(Batch batch, BatchAndSubBatchSummaryResponse response) {
        double batchFailedPercent = 0;
        double batchSuccessPercent = 0;

        if(CollectionUtils.isEmpty(response.getSubBatchSummaryList())){
            response.setSubBatchSummaryList(new ArrayList<>());
        }

        if(batch.getTotalTransactions() != null){
            batchFailedPercent = ((double) batch.getFailed()) / batch.getTotalTransactions() * 100;
            batchSuccessPercent = ((double) batch.getCompleted()) / batch.getTotalTransactions() * 100;
        }

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setRoundingMode(RoundingMode.FLOOR);

        SubBatchSummary subBatchSummary = new SubBatchSummary();
        subBatchSummary.setBatchId(batch.getBatchId());
        subBatchSummary.setRequestId(batch.getRequestId());
        subBatchSummary.setTotal(batch.getTotalTransactions());
        subBatchSummary.setTotalAmount(new BigDecimal(batch.getTotalAmount()));
        subBatchSummary.setOngoing(batch.getOngoing());
        subBatchSummary.setPendingAmount(new BigDecimal(batch.getOngoingAmount()));
        subBatchSummary.setSuccessful(batch.getCompleted());
        subBatchSummary.setSuccessfulAmount(new BigDecimal(batch.getCompletedAmount()));
        subBatchSummary.setFailed(batch.getFailed());
        subBatchSummary.setFailedAmount(new BigDecimal(batch.getFailedAmount()));
        subBatchSummary.setFile(batch.getRequestFile());
        subBatchSummary.setNotes(batch.getNote());
        subBatchSummary.setCreatedAt(batch.getStartedAt().toString());
//        subBatchSummary.setStatus();      => see how to set status
        subBatchSummary.setModes(batch.getPaymentMode());
        subBatchSummary.setPurpose(null);
        subBatchSummary.setSuccessPercentage(decimalFormat.format(batchSuccessPercent));
        subBatchSummary.setFailedPercentage(decimalFormat.format(batchFailedPercent));

        // set payerFsp, payeeFspList, approvedAmount, approvedCount

        return subBatchSummary;

    }

    private void updateResponseWithBatchInfo(Batch batch, BatchAndSubBatchSummaryResponse response) {
        double batchFailedPercent = 0;
        double batchSuccessPercent = 0;

        if(batch.getTotalTransactions() != null){
            batchFailedPercent = ((double) batch.getFailed()) / batch.getTotalTransactions() * 100;
            batchSuccessPercent = ((double) batch.getCompleted()) / batch.getTotalTransactions() * 100;
        }

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setRoundingMode(RoundingMode.FLOOR);

        response.setBatchId(batch.getBatchId());
        response.setRequestId(batch.getRequestId());
        response.setTotal(batch.getTotalTransactions());
        response.setTotalAmount(new BigDecimal(batch.getTotalAmount()));
        response.setOngoing(batch.getOngoing());
        response.setPendingAmount(new BigDecimal(batch.getOngoingAmount()));
        response.setSuccessful(batch.getCompleted());
        response.setSuccessfulAmount(new BigDecimal(batch.getCompletedAmount()));
        response.setFailed(batch.getFailed());
        response.setFailedAmount(new BigDecimal(batch.getFailedAmount()));
        response.setFile(batch.getResult_file());
        response.setNotes(batch.getNote());
        response.setCreatedAt(batch.getStartedAt().toString());
//        response.setStatus();     => see how to set status
        response.setModes(batch.getPaymentMode());
        response.setPurpose(null);
        response.setSuccessPercentage(decimalFormat.format(batchSuccessPercent));
        response.setFailedPercentage(decimalFormat.format(batchFailedPercent));

        // set payerFsp, generatedBy, generatedAt, totalSubBatches, approvedTransactionCount, approvedAmount
    }
}

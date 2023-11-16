package org.apache.fineract.service;

import org.apache.fineract.operations.PaymentBatchDetail;
import org.apache.fineract.operations.SubBatch;
import org.apache.fineract.response.BatchAndSubBatchSummaryResponse;
import org.apache.fineract.response.SubBatchSummary;

public interface BatchService {
    BatchAndSubBatchSummaryResponse getBatchAndSubBatchSummary(String batchId, String clientCorrelationId);
    PaymentBatchDetail getPaymentBathDetail(String batchId, String clientCorrelationId, int offset, int limit, String orderBy, String sortBy);
    SubBatchSummary getPaymentSubBatchDetail(String batchId, String subBatchId, String clientCorrelationId, int offset, int limit, String orderBy, String sortBy);
}

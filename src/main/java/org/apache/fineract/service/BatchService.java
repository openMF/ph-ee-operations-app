package org.apache.fineract.service;

import org.apache.fineract.response.BatchAndSubBatchSummaryResponse;

public interface BatchService {
    BatchAndSubBatchSummaryResponse getBatchAndSubBatchSummary(String batchId, String clientCorrelationId);
}

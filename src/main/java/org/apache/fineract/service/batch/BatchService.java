package org.apache.fineract.service.batch;

import org.apache.fineract.response.BatchWithSubBatchesAndTransactionsDetailResponse;

public interface BatchService {

    BatchWithSubBatchesAndTransactionsDetailResponse getBatchWithSubBatchesAndTransactionsDetail(String batchId, String clientCorrelationId, int pageNo, int pageSize, String sortBy, String orderBy);
}

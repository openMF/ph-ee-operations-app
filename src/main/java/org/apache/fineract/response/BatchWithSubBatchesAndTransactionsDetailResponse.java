package org.apache.fineract.response;

import com.azure.core.annotation.Get;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BatchWithSubBatchesAndTransactionsDetailResponse {

    String batchId;

    String payerFsp;

    String generatedAt;

    String generatedBy;

    Long totalTransactionCount;

    Long totalAmount;

    List<SubBatchDetail> subBatchDetails;


}

package org.apache.fineract.response;

import lombok.Getter;
import lombok.Setter;
import org.apache.fineract.operations.Transfer;

import java.util.List;

@Getter
@Setter
public class SubBatchDetailResponse {

    private String subBatchId;

    private String payerFsp;

    private String generatedAt;

    private String generatedBy;

    private Long totalTransactionCount;

    private Long totalAmount;

    private List<Transfer> content;
}

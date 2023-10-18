package org.apache.fineract.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class BatchAndSubBatchSummaryResponse {

    private String batchId;

    private String requestId;

    private Long total;

    private Long ongoing;

    private Long successful;

    private Long failed;

    private BigDecimal totalAmount;

    private BigDecimal pendingAmount;

    private BigDecimal successfulAmount;

    private BigDecimal failedAmount;

    private String file;

    private String notes;

    private String createdAt;

    private String status;

    private String modes;

    private String purpose;

    private String failedPercentage;

    private String successPercentage;

    private String payerFsp;
    private Set<String> payeeFsp;

    private String generatedBy;

    private String generatedAt;

    private String totalSubBatches;

    private Long approvedTransactionCount;

    private Long approvedAmount;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<SubBatchSummary> subBatchSummaryList;
    private Long totalInstructionCount;
}

package org.apache.fineract.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class SubBatchSummary {

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

    private String approvedAmount;

    private String approvedTransactionCount;

    private List<String> payeeFspList;



}

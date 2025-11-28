package org.apache.fineract.response;

import lombok.Getter;
import lombok.Setter;
import org.apache.fineract.operations.Instruction;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class SubBatchSummary {
    private String subBatchId;

    private String batchId;

    private Date startedAt;

    private Date completedAt;

    private String registeringInstitutionId;

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

    private Long approvedAmount;

    private Long approvedTransactionCount;

    private Set<String> payeeFspSet;

    private List<Instruction> instructionList;

    private String budgetAccount;

    private String generatedBy;

    private String generatedAt;

    private Long totalInstructionCount;

}

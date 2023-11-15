package org.apache.fineract.operations;

import lombok.Getter;
import lombok.Setter;
import org.apache.fineract.response.SubBatchSummary;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class PaymentBatchDetail {
    private String batchId;
    private String payerFsp;
    private String reportGeneratedBy;
    private String reportGeneratedAt;
    private List<SubBatchSummary> subBatchList;
    private List<Instruction> instructionList;
    private Long totalInstruction;
    private Long totalBatchAmount;
    private String clientCorrelationId;
}
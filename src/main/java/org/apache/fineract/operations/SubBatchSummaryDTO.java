package org.apache.fineract.operations;

import java.util.Date;
import java.util.List;

public class SubBatchSummaryDTO {
    private String batchId;
    private String xCorrelationId;
    private String payerFSP;
    private Date reportGeneratedAt;
    private String reportGeneratedBy;
    private List<SubBatch> subBatchList;
    private int totalNumberOfSubBatches;
    private int totalNumberOfInstructionsInSubBatch;
    private double totalAmountInSubBatch;
    private int totalApprovedPaymentsInSubBatch;
    private double totalApprovedAmountInSubBatch;

}

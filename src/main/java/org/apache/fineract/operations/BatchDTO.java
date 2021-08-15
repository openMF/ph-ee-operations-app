package org.apache.fineract.operations;

import java.util.Date;

public class BatchDTO {

    private String batchId;

    private String requestId;

    private Long totalTransactions;

    private Long ongoing;

    private Long failed;

    private Long completed;

    private String result_file;

    private Date resultGeneratedAt;

    private String note;

    public BatchDTO(String batchId, String requestId, Long totalTransactions, Long ongoing, Long failed, Long completed, String result_file, Date resultGeneratedAt, String note) {
        this.batchId = batchId;
        this.requestId = requestId;
        this.totalTransactions = totalTransactions;
        this.ongoing = ongoing;
        this.failed = failed;
        this.completed = completed;
        this.result_file = result_file;
        this.resultGeneratedAt = resultGeneratedAt;
        this.note = note;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Long getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(Long totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public Long getOngoing() {
        return ongoing;
    }

    public void setOngoing(Long ongoing) {
        this.ongoing = ongoing;
    }

    public Long getFailed() {
        return failed;
    }

    public void setFailed(Long failed) {
        this.failed = failed;
    }

    public Long getCompleted() {
        return completed;
    }

    public void setCompleted(Long completed) {
        this.completed = completed;
    }

    public String getResult_file() {
        return result_file;
    }

    public void setResult_file(String result_file) {
        this.result_file = result_file;
    }

    public Date getResultGeneratedAt() {
        return resultGeneratedAt;
    }

    public void setResultGeneratedAt(Date resultGeneratedAt) {
        this.resultGeneratedAt = resultGeneratedAt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}

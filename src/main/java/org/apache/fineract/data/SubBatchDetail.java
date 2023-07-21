package org.apache.fineract.data;

import java.util.Date;

public class SubBatchDetail {

    private String subBatchId;

    private Date startedAt;

    public String getSubBatchId() {
        return subBatchId;
    }

    public void setSubBatchId(String subBatchId) {
        this.subBatchId = subBatchId;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }
}

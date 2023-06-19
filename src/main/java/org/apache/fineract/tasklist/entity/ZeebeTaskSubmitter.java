package org.apache.fineract.tasklist.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity(name = "zeebe_task_submitter")
public class ZeebeTaskSubmitter {

    @EmbeddedId
    private ZeebeTaskSubmitterId id;

    public ZeebeTaskSubmitter() {
    }

    public ZeebeTaskSubmitter(ZeebeTaskSubmitterId id) {
        this.id = id;
    }

    public ZeebeTaskSubmitterId getId() {
        return id;
    }

    public void setId(ZeebeTaskSubmitterId id) {
        this.id = id;
    }
}

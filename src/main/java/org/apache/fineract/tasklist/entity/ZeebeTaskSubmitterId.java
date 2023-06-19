package org.apache.fineract.tasklist.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class ZeebeTaskSubmitterId implements Serializable {

    @Column(name = "task_id")
    private Long taskId;
    @Column(name = "user_name")
    private String userName;

    public ZeebeTaskSubmitterId() {
    }

    public ZeebeTaskSubmitterId(Long taskId, String userName) {
        this.taskId = taskId;
        this.userName = userName;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}

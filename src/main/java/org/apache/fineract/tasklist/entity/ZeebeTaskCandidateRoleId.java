package org.apache.fineract.tasklist.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;

import java.io.Serializable;

@Embeddable
public class ZeebeTaskCandidateRoleId implements Serializable {

    @Column(name = "task_id")
    private Long taskId;
    @Column(name = "role_name")
    private String roleName;

    public ZeebeTaskCandidateRoleId() {
    }

    public ZeebeTaskCandidateRoleId(Long taskId, String roleName) {
        this.taskId = taskId;
        this.roleName = roleName;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}

package org.apache.fineract.tasklist.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

@Entity(name = "zeebe_task_candidate_role")
public class ZeebeTaskCandidateRole {

    @EmbeddedId
    private ZeebeTaskCandidateRoleId id;

    public ZeebeTaskCandidateRole() {
    }

    public ZeebeTaskCandidateRole(ZeebeTaskCandidateRoleId id) {
        this.id = id;
    }

    public ZeebeTaskCandidateRoleId getId() {
        return id;
    }

    public void setId(ZeebeTaskCandidateRoleId id) {
        this.id = id;
    }

}

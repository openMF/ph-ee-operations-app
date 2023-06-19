package org.apache.fineract.tasklist.dto;

import java.util.List;

public class ZeebeTaskListDto {

    private Long id;
    private String variables;
    private long timestamp;
    private String name;
    private String description;
    private String taskForm;
    private String formData;
    private String assignee;
    private String endToEndId;
    private List<String> candidateRoles;
    private List<String> previousSubmitters;
    private Boolean isAssignable;
    private List<String> notAssignableReason;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVariables() {
        return variables;
    }

    public void setVariables(String variables) {
        this.variables = variables;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTaskForm() {
        return taskForm;
    }

    public void setTaskForm(String taskForm) {
        this.taskForm = taskForm;
    }

    public String getFormData() {
        return formData;
    }

    public void setFormData(String formData) {
        this.formData = formData;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getEndToEndId() {
        return endToEndId;
    }

    public void setEndToEndId(String endToEndId) {
        this.endToEndId = endToEndId;
    }

    public List<String> getCandidateRoles() {
        return candidateRoles;
    }

    public void setCandidateRoles(List<String> candidateRoles) {
        this.candidateRoles = candidateRoles;
    }

    public List<String> getPreviousSubmitters() {
        return previousSubmitters;
    }

    public void setPreviousSubmitters(List<String> previousSubmitters) {
        this.previousSubmitters = previousSubmitters;
    }

    public Boolean getAssignable() {
        return isAssignable;
    }

    public void setAssignable(Boolean assignable) {
        isAssignable = assignable;
    }

    public List<String> getNotAssignableReason() {
        return notAssignableReason;
    }

    public void setNotAssignableReason(List<String> notAssignableReason) {
        this.notAssignableReason = notAssignableReason;
    }
}

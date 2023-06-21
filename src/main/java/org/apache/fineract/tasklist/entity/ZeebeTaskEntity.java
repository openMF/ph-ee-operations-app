/*
 * Copyright © 2017 camunda services GmbH (info@camunda.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.fineract.tasklist.entity;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;

import java.util.Set;

@Entity(name = "zeebe_task")
public class ZeebeTaskEntity {

  @Id
  @Column(name = "id")
  private Long id;

  @Column(name = "variables")
  @Lob
  private String variables;

  @Column(name = "timestamp")
  private long timestamp;

  @Column(name = "name")
  private String name;

  @Column(name = "description")
  private String description;

  @Column(name = "task_form")
  @Lob
  private String taskForm;

  @Column(name = "form_data")
  @Lob
  private String formData;

  @Column(name = "assignee")
  private String assignee;

  @Column(name = "business_key")
  private String businessKey;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
  @JoinColumn(name = "task_id")
  private Set<ZeebeTaskCandidateRole> candidateRoles;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
  @JoinColumn(name = "task_id")
  private Set<ZeebeTaskSubmitter> previousSubmitters;

  public Long getId() {
    return id;
  }

  public void setId(Long key) {
    this.id = key;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public String getVariables() {
    return variables;
  }

  public void setVariables(String variables) {
    this.variables = variables;
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

  public String getBusinessKey() {
    return businessKey;
  }

  public void setBusinessKey(String endToEndId) {
    this.businessKey = endToEndId;
  }

  public Set<ZeebeTaskCandidateRole> getCandidateRoles() {
    return candidateRoles;
  }

  public Set<ZeebeTaskSubmitter> getPreviousSubmitters() {
    return previousSubmitters;
  }

  public void setPreviousSubmitters(Set<ZeebeTaskSubmitter> previousSubmitters) {
    this.previousSubmitters = previousSubmitters;
  }

  public void setCandidateRoles(Set<ZeebeTaskCandidateRole> candidateRoles) {
    this.candidateRoles = candidateRoles;


  }
}

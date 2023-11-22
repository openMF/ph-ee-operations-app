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
package org.apache.fineract.tasklist.repository;

import jakarta.transaction.Transactional;
import org.apache.fineract.tasklist.entity.ZeebeTaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

@Transactional
public interface ZeebeTaskRepository extends PagingAndSortingRepository<ZeebeTaskEntity, Long>, ListCrudRepository<ZeebeTaskEntity, Long> {


    @Query("SELECT DISTINCT task FROM zeebe_task task " +
            "WHERE task.assignee = :assignee " +
            "AND (:name IS NULL OR task.name = :name)"
    )
    Page<ZeebeTaskEntity> findMyTasks(@Param("assignee") String assignee,
                                      @Param("name") String name,
                                      Pageable pageable);

    @Query("SELECT DISTINCT task FROM zeebe_task task " +
            "LEFT OUTER JOIN zeebe_task_candidate_role role ON task.id = role.id.taskId " +
            "LEFT OUTER JOIN zeebe_task_submitter submitter ON task.id = submitter.id.taskId " +
            "WHERE (:assignee IS NULL OR task.assignee = :assignee) " +
            "AND (:candidateRole IS NULL OR role.id.roleName = :candidateRole) " +
            "AND (:previousSubmitter IS NULL OR submitter.id.userName = :previousSubmitter) " +
            "AND (:name IS NULL OR task.name = :name) " +
            "AND (:businessKey IS NULL OR task.businessKey = :businessKey)"
    )
    Page<ZeebeTaskEntity> findAll(@Param("assignee") String assignee,
                                  @Param("candidateRole") String candidateRole,
                                  @Param("previousSubmitter") String previousSubmitter,
                                  @Param("name") String name,
                                  @Param("businessKey") String businessKey,
                                  Pageable pageable);

    @Query("SELECT DISTINCT task FROM zeebe_task task " +
            "LEFT OUTER JOIN zeebe_task_candidate_role role ON task.id = role.id.taskId " +
            "LEFT OUTER JOIN zeebe_task_submitter submitter ON task.id = submitter.id.taskId " +
            "WHERE task.assignee IS NULL " +
            "AND (role.id.roleName is null OR role.id.roleName IN :roles) " +
            "AND NOT EXISTS (SELECT s FROM zeebe_task_submitter s WHERE s.id.taskId = task.id and s.id.userName = :userName) " +
            "AND (:assignee IS NULL OR task.assignee = :assignee) " +
            "AND (:candidateRole IS NULL OR role.id.roleName = :candidateRole) " +
            "AND (:previousSubmitter IS NULL OR submitter.id.userName = :previousSubmitter) " +
            "AND (:name IS NULL OR task.name = :name) " +
            "AND (:businessKey IS NULL OR task.businessKey = :businessKey)"
    )
    Page<ZeebeTaskEntity> findAllClaimableTask(@Param("userName") String userName,
                                               @Param("roles") Collection<String> roles,
                                               @Param("assignee") String assignee,
                                               @Param("candidateRole") String candidateRole,
                                               @Param("previousSubmitter") String previousSubmitter,
                                               @Param("name") String name,
                                               @Param("businessKey") String businessKey,
                                               Pageable pageable);

    @Query("SELECT DISTINCT task FROM zeebe_task task " +
            "LEFT OUTER JOIN zeebe_task_candidate_role role ON task.id = role.id.taskId " +
            "LEFT OUTER JOIN zeebe_task_submitter submitter ON task.id = submitter.id.taskId " +
            "WHERE task.assignee IS NULL " +
            "AND role.id.roleName IS NULL " +
            "AND NOT EXISTS (SELECT submitter FROM zeebe_task_submitter submitter WHERE submitter.id.taskId = task.id and submitter.id.userName = :userName) " +
            "AND (:assignee IS NULL OR task.assignee = :assignee) " +
            "AND (:candidateRole IS NULL OR role.id.roleName = :candidateRole) " +
            "AND (:previousSubmitter IS NULL OR submitter.id.userName = :previousSubmitter) " +
            "AND (:name IS NULL OR task.name = :name) " +
            "AND (:businessKey IS NULL OR task.businessKey = :businessKey)"
    )
    Page<ZeebeTaskEntity> findAllClaimableTask(@Param("userName") String userName,
                                               @Param("assignee") String assignee,
                                               @Param("candidateRole") String candidateRole,
                                               @Param("previousSubmitter") String previousSubmitter,
                                               @Param("name") String name,
                                               @Param("businessKey") String businessKey,
                                               Pageable pageable);


    List<ZeebeTaskEntity> findAllByIdIn(@Param("keys") List<Long> ids);
}

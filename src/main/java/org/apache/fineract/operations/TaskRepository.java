package org.apache.fineract.operations;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface TaskRepository extends CrudRepository<Task, Long> {

    List<Task> findByWorkflowInstanceKeyOrderByTimestamp(Long workflowInstanceKey);

}

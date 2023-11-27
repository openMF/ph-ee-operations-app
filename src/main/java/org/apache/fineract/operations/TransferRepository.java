package org.apache.fineract.operations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TransferRepository extends JpaRepository<Transfer, Long>, JpaSpecificationExecutor {

    Transfer findFirstByWorkflowInstanceKey(Long workflowInstanceKey);

    Transfer findFirstByTransactionIdAndDirection(String transactionId, String direction);

    Optional<Transfer> findFirstByTransactionId(String transactionId);

    List<Transfer> findAllByBatchId(String batchId);

    Page<Transfer> findAllByBatchIdAndStatus(String batchId, String status, Pageable pageable);

    Page<Transfer> findAllByBatchId(String batchId, Pageable pageable);
    Long countAllByBatchId(String batchId);
    Page<Transfer> findAll(Pageable pageable);

}

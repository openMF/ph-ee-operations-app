package org.apache.fineract.operations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransferRepository extends JpaRepository<Transfer, Long>, JpaSpecificationExecutor {

    Transfer findFirstByWorkflowInstanceKey(Long workflowInstanceKey);

    Transfer findFirstByTransactionIdAndDirection(String transactionId, String direction);

    @Query("SELECT t FROM Transfer t WHERE t.transactionId = :transactionId AND :direction = 'INCOMING' AND recallDirection IS NULL")
    Transfer findIcomingTransfersForRecall(@Param("transactionId") String transactionId);
}
package org.apache.fineract.card.repository;

import org.apache.fineract.card.entity.CardTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardTransactionRepository extends JpaRepository<CardTransaction, String> {

    CardTransaction findByWorkflowInstanceKey(String workflowInstanceKey);

}
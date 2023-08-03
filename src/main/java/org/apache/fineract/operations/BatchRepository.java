package org.apache.fineract.operations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;

import java.util.Date;
import java.util.List;

public interface BatchRepository extends JpaRepository<Batch, Long>, JpaSpecificationExecutor<Batch> {

    Batch findByWorkflowInstanceKey(Long workflowInstanceKey);

    @Query("SELECT bt FROM Batch bt WHERE bt.batchId = :batchId and bt.subBatchId is null")
    Batch findByBatchId(String batchId);

    List<Batch> findAllByBatchId(String batchId);

	@Query(value = "SELECT bt FROM Batch bt ORDER BY :orderBy")
	List<Batch> findAll(String orderBy, Pageable pageable);

    @Query(value = "SELECT bt FROM Batch bt WHERE bt.startedAt > :dateFrom ORDER BY :orderBy")
    List<Batch> findAllFilterDateFrom(Date dateFrom, String orderBy, Pageable pageable);

    @Query(value = "SELECT bt FROM Batch bt WHERE bt.startedAt < :dateTo ORDER BY :orderBy")
    List<Batch> findAllFilterDateTo(Date dateTo, String orderBy, Pageable pageable);

    @Query(value = "SELECT bt FROM Batch bt WHERE bt.startedAt BETWEEN :dateFrom AND :dateTo ORDER BY :orderBy")
    List<Batch> findAllFilterDateBetween(Date dateFrom, Date dateTo, String orderBy, Pageable pageable);
}

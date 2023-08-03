package org.apache.fineract.operations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public interface BatchRepository extends JpaRepository<Batch, Long>, JpaSpecificationExecutor<Batch> {

    Batch findByWorkflowInstanceKey(Long workflowInstanceKey);

    @Query("SELECT bt FROM Batch bt WHERE bt.batchId = :batchId and bt.subBatchId is null")
    Batch findByBatchId(String batchId);

    List<Batch> findAllByBatchId(String batchId);

	@Query(value = "SELECT bt FROM Batch bt " +
            "WHERE bt.registeringInstitutionId LIKE :registeringInstitutionId AND " +
            "bt.payerFsp LIKE :payerFsp")
	List<Batch> findAllPaged(String registeringInstitutionId, String payerFsp, Pageable pageable);

    @Query(value = "SELECT bt FROM Batch bt WHERE bt.startedAt >= :dateFrom AND " +
            "bt.registeringInstitutionId LIKE :registeringInstitutionId AND " +
            "bt.payerFsp LIKE :payerFsp")
    List<Batch> findAllFilterDateFrom(Date dateFrom, String registeringInstitutionId, String payerFsp, Pageable pageable);

    @Query(value = "SELECT bt FROM Batch bt WHERE bt.startedAt <= :dateTo AND " +
            "bt.registeringInstitutionId LIKE :registeringInstitutionId AND " +
            "bt.payerFsp LIKE :payerFsp")
    List<Batch> findAllFilterDateTo(Date dateTo, String registeringInstitutionId, String payerFsp, Pageable pageable);

    @Query(value = "SELECT bt FROM Batch bt WHERE bt.startedAt BETWEEN :dateFrom AND :dateTo AND " +
            "bt.registeringInstitutionId LIKE :registeringInstitutionId AND " +
            "bt.payerFsp LIKE :payerFsp")
    List<Batch> findAllFilterDateBetween(Date dateFrom, Date dateTo, String registeringInstitutionId, String payerFsp, Pageable pageable);


    @Query(value = "SELECT COUNT(bt) as totalCount, SUM(bt.totalTransactions) as totalTransactions FROM Batch bt " +
            "WHERE bt.registeringInstitutionId LIKE :registeringInstitutionId AND " +
            "bt.payerFsp LIKE :payerFsp")
    HashMap<String, Integer> countTransaction(String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT COUNT(bt) as totalCount, SUM(bt.totalTransactions) as totalTransactions FROM Batch bt " +
            "WHERE bt.startedAt >= :dateFrom AND " +
            "bt.registeringInstitutionId LIKE :registeringInstitutionId AND " +
            "bt.payerFsp LIKE :payerFsp")
    HashMap<String, Integer> countTransactionDateFrom(Date dateFrom, String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT COUNT(bt) as totalCount, SUM(bt.totalTransactions) as totalTransactions FROM Batch bt " +
            "WHERE bt.startedAt <= :dateTo AND " +
            "bt.registeringInstitutionId LIKE :registeringInstitutionId AND " +
            "bt.payerFsp LIKE :payerFsp")
    HashMap<String, Integer> countTransactionDateTo(Date dateTo, String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT COUNT(bt) as totalCount, SUM(bt.totalTransactions) as totalTransactions FROM Batch bt " +
            "WHERE bt.startedAt BETWEEN :dateFrom AND :dateTo AND " +
            "bt.registeringInstitutionId LIKE :registeringInstitutionId AND " +
            "bt.payerFsp LIKE :payerFsp")
    HashMap<String, Integer> countTransactionDateBetween(Date dateFrom, Date dateTo, String registeringInstitutionId, String payerFsp);

}

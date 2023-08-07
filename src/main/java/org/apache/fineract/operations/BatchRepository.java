package org.apache.fineract.operations;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import java.util.Date;
import java.util.List;

public interface BatchRepository extends JpaRepository<Batch, Long>, JpaSpecificationExecutor<Batch> {

    Batch findByWorkflowInstanceKey(Long workflowInstanceKey);

    @Query("SELECT bt FROM Batch bt WHERE bt.batchId = :batchId and bt.subBatchId is null")
    Batch findByBatchId(String batchId);

    List<Batch> findAllByBatchId(String batchId);

    @Query("SELECT bt FROM Batch bt " +
            "WHERE bt.registeringInstitutionId LIKE :registeringInstituteId AND bt.payerFsp LIKE :payerFsp AND " +
            "bt.batchId LIKE :batchId AND bt.subBatchId IS NULL")
    List<Batch> findAllBatch(String registeringInstituteId, String payerFsp, String batchId, Pageable pageable);

    @Query(value = "SELECT bt FROM Batch bt WHERE bt.startedAt >= :dateFrom AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND " +
            "bt.batchId LIKE :batchId AND bt.subBatchId IS NULL")
    List<Batch> findAllFilterDateFrom(Date dateFrom, String registeringInstitutionId, String payerFsp, String batchId, Pageable pageable);

    @Query(value = "SELECT bt FROM Batch bt WHERE bt.startedAt <= :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND " +
            "bt.batchId LIKE :batchId AND bt.subBatchId IS NULL")
    List<Batch> findAllFilterDateTo(Date dateTo, String registeringInstitutionId, String payerFsp, String batchId, Pageable pageable);

    @Query(value = "SELECT bt FROM Batch bt WHERE bt.startedAt BETWEEN :dateFrom AND :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND " +
            "bt.batchId LIKE :batchId AND bt.subBatchId IS NULL")
    List<Batch> findAllFilterDateBetween(Date dateFrom, Date dateTo, String registeringInstitutionId, String payerFsp, String batchId, Pageable pageable);


    @Query(value = "SELECT COUNT(bt) FROM Batch bt " +
            "WHERE (bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND " +
            "bt.batchId LIKE :batchId AND bt.subBatchId IS NULL")
    Long getTotalBatches(String registeringInstitutionId, String payerFsp, String batchId);

    @Query(value = "SELECT COUNT(bt) FROM Batch bt " +
            "WHERE bt.startedAt >= :dateFrom AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND " +
            "bt.batchId LIKE :batchId AND bt.subBatchId IS NULL")
    Long getTotalBatchesDateFrom(Date dateFrom, String registeringInstitutionId, String payerFsp, String batchId);

    @Query(value = "SELECT COUNT(bt) FROM Batch bt " +
            "WHERE bt.startedAt <= :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND " +
            "bt.batchId LIKE :batchId AND bt.subBatchId IS NULL")
    Long getTotalBatchesDateTo(Date dateTo, String registeringInstitutionId, String payerFsp, String batchId);

    @Query(value = "SELECT COUNT(bt) FROM Batch bt " +
            "WHERE bt.startedAt BETWEEN :dateFrom AND :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND " +
            "bt.batchId LIKE :batchId AND bt.subBatchId IS NULL")
    Long getTotalBatchesDateBetween(Date dateFrom, Date dateTo, String registeringInstitutionId, String payerFsp, String batchId);

    @Query(value = "SELECT SUM(bt.totalTransactions) FROM Batch bt " +
            "WHERE (bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND " +
            "bt.batchId LIKE :batchId AND bt.subBatchId IS NULL")
    Long getTotalTransactions(String registeringInstitutionId, String payerFsp, String batchId);

    @Query(value = "SELECT SUM(bt.totalTransactions) FROM Batch bt " +
            "WHERE bt.startedAt >= :dateFrom AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND " +
            "bt.batchId LIKE :batchId AND bt.subBatchId IS NULL")
    Long getTotalTransactionsDateFrom(Date dateFrom, String registeringInstitutionId, String payerFsp, String batchId);

    @Query(value = "SELECT SUM(bt.totalTransactions) FROM Batch bt " +
            "WHERE bt.startedAt <= :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND " +
            "bt.batchId LIKE :batchId AND bt.subBatchId IS NULL")
    Long getTotalTransactionsDateTo(Date dateTo, String registeringInstitutionId, String payerFsp, String batchId);

    @Query(value = "SELECT SUM(bt.totalTransactions) FROM Batch bt " +
            "WHERE bt.startedAt BETWEEN :dateFrom AND :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND " +
            "bt.batchId LIKE :batchId AND bt.subBatchId IS NULL")
    Long getTotalTransactionsDateBetween(Date dateFrom, Date dateTo, String registeringInstitutionId, String payerFsp, String batchId);

    @Query(value = "SELECT SUM(bt.totalAmount) FROM Batch bt " +
            "WHERE (bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND " +
            "bt.batchId LIKE :batchId AND bt.subBatchId IS NULL")
    Long getTotalAmount(String registeringInstitutionId, String payerFsp, String batchId);

    @Query(value = "SELECT SUM(bt.totalAmount) FROM Batch bt " +
            "WHERE bt.startedAt >= :dateFrom AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND " +
            "bt.batchId LIKE :batchId AND bt.subBatchId IS NULL")
    Long getTotalAmountDateFrom(Date dateFrom, String registeringInstitutionId, String payerFsp, String batchId);

    @Query(value = "SELECT SUM(bt.totalAmount) FROM Batch bt " +
            "WHERE bt.startedAt <= :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND " +
            "bt.batchId LIKE :batchId AND bt.subBatchId IS NULL")
    Long getTotalAmountDateTo(Date dateTo, String registeringInstitutionId, String payerFsp, String batchId);

    @Query(value = "SELECT SUM(bt.totalAmount) FROM Batch bt " +
            "WHERE bt.startedAt BETWEEN :dateFrom AND :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND " +
            "bt.batchId LIKE :batchId AND bt.subBatchId IS NULL")
    Long getTotalAmountDateBetween(Date dateFrom, Date dateTo, String registeringInstitutionId, String payerFsp, String batchId);

    @Query(value = "SELECT SUM(bt.approvedAmount) FROM Batch bt " +
            "WHERE (bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND " +
            "bt.batchId LIKE :batchId AND bt.subBatchId IS NULL")
    Long getTotalApprovedAmount(String registeringInstitutionId, String payerFsp, String batchId);

    @Query(value = "SELECT SUM(bt.approvedAmount) FROM Batch bt " +
            "WHERE bt.startedAt >= :dateFrom AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND " +
            "bt.batchId LIKE :batchId AND bt.subBatchId IS NULL")
    Long getTotalApprovedAmountDateFrom(Date dateFrom, String registeringInstitutionId, String payerFsp, String batchId);

    @Query(value = "SELECT SUM(bt.approvedAmount) FROM Batch bt " +
            "WHERE bt.startedAt <= :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND " +
            "bt.batchId LIKE :batchId AND bt.subBatchId IS NULL")
    Long getTotalApprovedAmountDateTo(Date dateTo, String registeringInstitutionId, String payerFsp, String batchId);

    @Query(value = "SELECT SUM(bt.approvedAmount) FROM Batch bt " +
            "WHERE bt.startedAt BETWEEN :dateFrom AND :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND " +
            "bt.batchId LIKE :batchId AND bt.subBatchId IS NULL")
    Long getTotalApprovedAmountDateBetween(Date dateFrom, Date dateTo, String registeringInstitutionId, String payerFsp, String batchId);

    @Query(value = "SELECT SUM(bt.approvedCount) FROM Batch bt " +
            "WHERE (bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND " +
            "bt.batchId LIKE :batchId AND bt.subBatchId IS NULL")
    Long getTotalApprovedCount(String registeringInstitutionId, String payerFsp, String batchId);

    @Query(value = "SELECT SUM(bt.approvedCount) FROM Batch bt " +
            "WHERE bt.startedAt >= :dateFrom AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND " +
            "bt.batchId LIKE :batchId AND bt.subBatchId IS NULL")
    Long getTotalApprovedCountDateFrom(Date dateFrom, String registeringInstitutionId, String payerFsp, String batchId);

    @Query(value = "SELECT SUM(bt.approvedCount) FROM Batch bt " +
            "WHERE bt.startedAt <= :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND " +
            "bt.batchId LIKE :batchId AND bt.subBatchId IS NULL")
    Long getTotalApprovedCountDateTo(Date dateTo, String registeringInstitutionId, String payerFsp, String batchId);

    @Query(value = "SELECT SUM(bt.approvedCount) FROM Batch bt " +
            "WHERE bt.startedAt BETWEEN :dateFrom AND :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND " +
            "bt.batchId LIKE :batchId AND bt.subBatchId IS NULL")
    Long getTotalApprovedCountDateBetween(Date dateFrom, Date dateTo, String registeringInstitutionId, String payerFsp, String batchId);
}

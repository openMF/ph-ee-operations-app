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
            "WHERE bt.registeringInstitutionId LIKE :registeringInstituteId AND bt.payerFsp LIKE :payerFsp AND bt.batchId LIKE :batchId")
    List<Batch> findAllBatch(String registeringInstituteId, String payerFsp, String batchId, Pageable pageable);

    @Query(value = "SELECT bt FROM Batch bt WHERE bt.startedAt >= :dateFrom AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND bt.batchId LIKE :batchId")
    List<Batch> findAllFilterDateFrom(Date dateFrom, String registeringInstitutionId, String payerFsp, String batchId, Pageable pageable);

    @Query(value = "SELECT bt FROM Batch bt WHERE bt.startedAt <= :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND bt.batchId LIKE :batchId")
    List<Batch> findAllFilterDateTo(Date dateTo, String registeringInstitutionId, String payerFsp, String batchId, Pageable pageable);

    @Query(value = "SELECT bt FROM Batch bt WHERE bt.startedAt BETWEEN :dateFrom AND :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND bt.batchId LIKE :batchId")
    List<Batch> findAllFilterDateBetween(Date dateFrom, Date dateTo, String registeringInstitutionId, String payerFsp, String batchId, Pageable pageable);


    @Query(value = "SELECT COUNT(bt) FROM Batch bt " +
            "WHERE (bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND bt.batchId LIKE :batchId")
    Long getTotalBatches(String registeringInstitutionId, String payerFsp, String batchId);

    @Query(value = "SELECT COUNT(bt) FROM Batch bt " +
            "WHERE bt.startedAt >= :dateFrom AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND bt.batchId LIKE :batchId")
    Long getTotalBatchesDateFrom(Date dateFrom, String registeringInstitutionId, String payerFsp, String batchId);

    @Query(value = "SELECT COUNT(bt) FROM Batch bt " +
            "WHERE bt.startedAt <= :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND bt.batchId LIKE :batchId")
    Long getTotalBatchesDateTo(Date dateTo, String registeringInstitutionId, String payerFsp, String batchId);

    @Query(value = "SELECT COUNT(bt) FROM Batch bt " +
            "WHERE bt.startedAt BETWEEN :dateFrom AND :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND bt.batchId LIKE :batchId")
    Long getTotalBatchesDateBetween(Date dateFrom, Date dateTo, String registeringInstitutionId, String payerFsp, String batchId);

    @Query(value = "SELECT SUM(bt.totalTransactions) FROM Batch bt " +
            "WHERE (bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND bt.batchId LIKE :batchId")
    Long getTotalTransactions(String registeringInstitutionId, String payerFsp, String batchId);

    @Query(value = "SELECT SUM(bt.totalTransactions) FROM Batch bt " +
            "WHERE bt.startedAt >= :dateFrom AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND bt.batchId LIKE :batchId")
    Long getTotalTransactionsDateFrom(Date dateFrom, String registeringInstitutionId, String payerFsp, String batchId);

    @Query(value = "SELECT SUM(bt.totalTransactions) FROM Batch bt " +
            "WHERE bt.startedAt <= :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND bt.batchId LIKE :batchId")
    Long getTotalTransactionsDateTo(Date dateTo, String registeringInstitutionId, String payerFsp, String batchId);

    @Query(value = "SELECT SUM(bt.totalTransactions) FROM Batch bt " +
            "WHERE bt.startedAt BETWEEN :dateFrom AND :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND bt.batchId LIKE :batchId")
    Long getTotalTransactionsDateBetween(Date dateFrom, Date dateTo, String registeringInstitutionId, String payerFsp, String batchId);

    @Query(value = "SELECT SUM(bt.totalAmount) FROM Batch bt " +
            "WHERE (bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND bt.batchId LIKE :batchId")
    Long getTotalAmount(String registeringInstitutionId, String payerFsp, String batchId);

    @Query(value = "SELECT SUM(bt.totalAmount) FROM Batch bt " +
            "WHERE bt.startedAt >= :dateFrom AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND bt.batchId LIKE :batchId")
    Long getTotalAmountDateFrom(Date dateFrom, String registeringInstitutionId, String payerFsp, String batchId);

    @Query(value = "SELECT SUM(bt.totalAmount) FROM Batch bt " +
            "WHERE bt.startedAt <= :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND bt.batchId LIKE :batchId")
    Long getTotalAmountDateTo(Date dateTo, String registeringInstitutionId, String payerFsp, String batchId);

    @Query(value = "SELECT SUM(bt.totalAmount) FROM Batch bt " +
            "WHERE bt.startedAt BETWEEN :dateFrom AND :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND bt.batchId LIKE :batchId")
    Long getTotalAmountDateBetween(Date dateFrom, Date dateTo, String registeringInstitutionId, String payerFsp, String batchId);

    @Query(value = "SELECT SUM(bt.approvedAmount) FROM Batch bt " +
            "WHERE (bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND bt.batchId LIKE :batchId")
    Long getTotalApprovedAmount(String registeringInstitutionId, String payerFsp, String batchId);

    @Query(value = "SELECT SUM(bt.approvedAmount) FROM Batch bt " +
            "WHERE bt.startedAt >= :dateFrom AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND bt.batchId LIKE :batchId")
    Long getTotalApprovedAmountDateFrom(Date dateFrom, String registeringInstitutionId, String payerFsp, String batchId);

    @Query(value = "SELECT SUM(bt.approvedAmount) FROM Batch bt " +
            "WHERE bt.startedAt <= :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND bt.batchId LIKE :batchId")
    Long getTotalApprovedAmountDateTo(Date dateTo, String registeringInstitutionId, String payerFsp, String batchId);

    @Query(value = "SELECT SUM(bt.approvedAmount) FROM Batch bt " +
            "WHERE bt.startedAt BETWEEN :dateFrom AND :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND bt.batchId LIKE :batchId")
    Long getTotalApprovedAmountDateBetween(Date dateFrom, Date dateTo, String registeringInstitutionId, String payerFsp, String batchId);

    @Query(value = "SELECT SUM(bt.approvedCount) FROM Batch bt " +
            "WHERE (bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND bt.batchId LIKE :batchId")
    Long getTotalApprovedCount(String registeringInstitutionId, String payerFsp, String batchId);

    @Query(value = "SELECT SUM(bt.approvedCount) FROM Batch bt " +
            "WHERE bt.startedAt >= :dateFrom AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND bt.batchId LIKE :batchId")
    Long getTotalApprovedCountDateFrom(Date dateFrom, String registeringInstitutionId, String payerFsp, String batchId);

    @Query(value = "SELECT SUM(bt.approvedCount) FROM Batch bt " +
            "WHERE bt.startedAt <= :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND bt.batchId LIKE :batchId")
    Long getTotalApprovedCountDateTo(Date dateTo, String registeringInstitutionId, String payerFsp, String batchId);

    @Query(value = "SELECT SUM(bt.approvedCount) FROM Batch bt " +
            "WHERE bt.startedAt BETWEEN :dateFrom AND :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp) AND bt.batchId LIKE :batchId")
    Long getTotalApprovedCountDateBetween(Date dateFrom, Date dateTo, String registeringInstitutionId, String payerFsp, String batchId);
}

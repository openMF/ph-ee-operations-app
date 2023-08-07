package org.apache.fineract.operations;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    @Query(value = "SELECT bt FROM Batch bt " +
            "WHERE (bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp)")
    List<Batch> findAllPaged(String registeringInstitutionId, String payerFsp, Pageable pageable);

    @Query(value = "SELECT bt FROM Batch bt WHERE bt.startedAt >= :dateFrom AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp)")
    List<Batch> findAllFilterDateFrom(Date dateFrom, String registeringInstitutionId, String payerFsp, Pageable pageable);

    @Query(value = "SELECT bt FROM Batch bt WHERE bt.startedAt <= :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp)")
    List<Batch> findAllFilterDateTo(Date dateTo, String registeringInstitutionId, String payerFsp, Pageable pageable);

    @Query(value = "SELECT bt FROM Batch bt WHERE bt.startedAt BETWEEN :dateFrom AND :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp)")
    List<Batch> findAllFilterDateBetween(Date dateFrom, Date dateTo, String registeringInstitutionId, String payerFsp, Pageable pageable);


    @Query(value = "SELECT COUNT(bt) FROM Batch bt " +
            "WHERE (bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp)")
    Long getTotalBatches(String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT COUNT(bt) FROM Batch bt " +
            "WHERE bt.startedAt >= :dateFrom AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp)")
    Long getTotalBatchesDateFrom(Date dateFrom, String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT COUNT(bt) FROM Batch bt " +
            "WHERE bt.startedAt <= :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp)")
    Long getTotalBatchesDateTo(Date dateTo, String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT COUNT(bt) FROM Batch bt " +
            "WHERE bt.startedAt BETWEEN :dateFrom AND :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp)")
    Long getTotalBatchesDateBetween(Date dateFrom, Date dateTo, String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT SUM(bt.totalTransactions) FROM Batch bt " +
            "WHERE (bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp)")
    Long getTotalTransactions(String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT SUM(bt.totalTransactions) FROM Batch bt " +
            "WHERE bt.startedAt >= :dateFrom AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp)")
    Long getTotalTransactionsDateFrom(Date dateFrom, String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT SUM(bt.totalTransactions) FROM Batch bt " +
            "WHERE bt.startedAt <= :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp)")
    Long getTotalTransactionsDateTo(Date dateTo, String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT SUM(bt.totalTransactions) FROM Batch bt " +
            "WHERE bt.startedAt BETWEEN :dateFrom AND :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp)")
    Long getTotalTransactionsDateBetween(Date dateFrom, Date dateTo, String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT SUM(bt.totalAmount) FROM Batch bt " +
            "WHERE (bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp)")
    Long getTotalAmount(String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT SUM(bt.totalAmount) FROM Batch bt " +
            "WHERE bt.startedAt >= :dateFrom AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp)")
    Long getTotalAmountDateFrom(Date dateFrom, String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT SUM(bt.totalAmount) FROM Batch bt " +
            "WHERE bt.startedAt <= :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp)")
    Long getTotalAmountDateTo(Date dateTo, String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT SUM(bt.totalAmount) FROM Batch bt " +
            "WHERE bt.startedAt BETWEEN :dateFrom AND :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp)")
    Long getTotalAmountDateBetween(Date dateFrom, Date dateTo, String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT SUM(bt.approvedAmount) FROM Batch bt " +
            "WHERE (bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp)")
    Long getTotalApprovedAmount(String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT SUM(bt.approvedAmount) FROM Batch bt " +
            "WHERE bt.startedAt >= :dateFrom AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp)")
    Long getTotalApprovedAmountDateFrom(Date dateFrom, String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT SUM(bt.approvedAmount) FROM Batch bt " +
            "WHERE bt.startedAt <= :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp)")
    Long getTotalApprovedAmountDateTo(Date dateTo, String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT SUM(bt.approvedAmount) FROM Batch bt " +
            "WHERE bt.startedAt BETWEEN :dateFrom AND :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp)")
    Long getTotalApprovedAmountDateBetween(Date dateFrom, Date dateTo, String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT SUM(bt.approvedCount) FROM Batch bt " +
            "WHERE (bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp)")
    Long getTotalApprovedCount(String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT SUM(bt.approvedCount) FROM Batch bt " +
            "WHERE bt.startedAt >= :dateFrom AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp)")
    Long getTotalApprovedCountDateFrom(Date dateFrom, String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT SUM(bt.approvedCount) FROM Batch bt " +
            "WHERE bt.startedAt <= :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp)")
    Long getTotalApprovedCountDateTo(Date dateTo, String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT SUM(bt.approvedCount) FROM Batch bt " +
            "WHERE bt.startedAt BETWEEN :dateFrom AND :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId) AND (bt.payerFsp LIKE :payerFsp)")
    Long getTotalApprovedCountDateBetween(Date dateFrom, Date dateTo, String registeringInstitutionId, String payerFsp);

}

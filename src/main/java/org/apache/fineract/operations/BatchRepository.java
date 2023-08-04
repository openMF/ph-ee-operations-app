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

	@Query(value = "SELECT bt FROM Batch bt " +
            "WHERE (bt.registeringInstitutionId = :registeringInstitutionId) AND " +
            "(bt.payerFsp = :payerFsp)")
	List<Batch> findAllPaged(String registeringInstitutionId, String payerFsp, Pageable pageable);

    @Query(value = "SELECT bt FROM Batch bt WHERE bt.startedAt >= :dateFrom AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId or bt.registeringInstitutionId IS NULL) AND " +
            "(bt.payerFsp LIKE :payerFsp or bt.payerFsp IS NULL)")
    List<Batch> findAllFilterDateFrom(Date dateFrom, String registeringInstitutionId, String payerFsp, Pageable pageable);

    @Query(value = "SELECT bt FROM Batch bt WHERE bt.startedAt <= :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId or bt.registeringInstitutionId IS NULL) AND " +
            "(bt.payerFsp LIKE :payerFsp or bt.payerFsp IS NULL)")
    List<Batch> findAllFilterDateTo(Date dateTo, String registeringInstitutionId, String payerFsp, Pageable pageable);

    @Query(value = "SELECT bt FROM Batch bt WHERE bt.startedAt BETWEEN :dateFrom AND :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId or bt.registeringInstitutionId IS NULL) AND " +
            "(bt.payerFsp LIKE :payerFsp or bt.payerFsp IS NULL)")
    List<Batch> findAllFilterDateBetween(Date dateFrom, Date dateTo, String registeringInstitutionId, String payerFsp, Pageable pageable);


    @Query(value = "SELECT COUNT(bt) FROM Batch bt " +
            "WHERE (bt.registeringInstitutionId LIKE :registeringInstitutionId or bt.registeringInstitutionId IS NULL) AND " +
            "(bt.payerFsp LIKE :payerFsp OR bt.payerFsp IS NULL)")
    Long getTotalBatches(String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT COUNT(bt) FROM Batch bt " +
            "WHERE bt.startedAt >= :dateFrom AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId or bt.registeringInstitutionId IS NULL) AND " +
            "(bt.payerFsp LIKE :payerFsp or bt.payerFsp IS NULL)")
    Long getTotalBatchesDateFrom(Date dateFrom, String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT COUNT(bt) FROM Batch bt " +
            "WHERE bt.startedAt <= :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId or bt.registeringInstitutionId IS NULL) AND " +
            "(bt.payerFsp LIKE :payerFsp or bt.payerFsp IS NULL)")
    Long getTotalBatchesDateTo(Date dateTo, String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT COUNT(bt) FROM Batch bt " +
            "WHERE bt.startedAt BETWEEN :dateFrom AND :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId or bt.registeringInstitutionId IS NULL) AND " +
            "(bt.payerFsp LIKE :payerFsp or bt.payerFsp IS NULL)")
    Long getTotalBatchesDateBetween(Date dateFrom, Date dateTo, String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT SUM(bt.totalTransactions) FROM Batch bt " +
            "WHERE (bt.registeringInstitutionId LIKE :registeringInstitutionId or bt.registeringInstitutionId IS NULL) AND " +
            "(bt.payerFsp LIKE :payerFsp OR bt.payerFsp IS NULL)")
    Long getTotalTransactions(String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT SUM(bt.totalTransactions) FROM Batch bt " +
            "WHERE bt.startedAt >= :dateFrom AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId or bt.registeringInstitutionId IS NULL) AND " +
            "(bt.payerFsp LIKE :payerFsp or bt.payerFsp IS NULL)")
    Long getTotalTransactionsDateFrom(Date dateFrom, String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT SUM(bt.totalTransactions) FROM Batch bt " +
            "WHERE bt.startedAt <= :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId or bt.registeringInstitutionId IS NULL) AND " +
            "(bt.payerFsp LIKE :payerFsp or bt.payerFsp IS NULL)")
    Long getTotalTransactionsDateTo(Date dateTo, String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT SUM(bt.totalTransactions) FROM Batch bt " +
            "WHERE bt.startedAt BETWEEN :dateFrom AND :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId or bt.registeringInstitutionId IS NULL) AND " +
            "(bt.payerFsp LIKE :payerFsp or bt.payerFsp IS NULL)")
    Long getTotalTransactionsDateBetween(Date dateFrom, Date dateTo, String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT SUM(bt.totalAmount) FROM Batch bt " +
            "WHERE (bt.registeringInstitutionId LIKE :registeringInstitutionId or bt.registeringInstitutionId IS NULL) AND " +
            "(bt.payerFsp LIKE :payerFsp OR bt.payerFsp IS NULL)")
    Long getTotalAmount(String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT SUM(bt.totalAmount) FROM Batch bt " +
            "WHERE bt.startedAt >= :dateFrom AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId or bt.registeringInstitutionId IS NULL) AND " +
            "(bt.payerFsp LIKE :payerFsp or bt.payerFsp IS NULL)")
    Long getTotalAmountDateFrom(Date dateFrom, String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT SUM(bt.totalAmount) FROM Batch bt " +
            "WHERE bt.startedAt <= :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId or bt.registeringInstitutionId IS NULL) AND " +
            "(bt.payerFsp LIKE :payerFsp or bt.payerFsp IS NULL)")
    Long getTotalAmountDateTo(Date dateTo, String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT SUM(bt.totalAmount) FROM Batch bt " +
            "WHERE bt.startedAt BETWEEN :dateFrom AND :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId or bt.registeringInstitutionId IS NULL) AND " +
            "(bt.payerFsp LIKE :payerFsp or bt.payerFsp IS NULL)")
    Long getTotalAmountDateBetween(Date dateFrom, Date dateTo, String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT SUM(bt.approvedAmount) FROM Batch bt " +
            "WHERE (bt.registeringInstitutionId LIKE :registeringInstitutionId or bt.registeringInstitutionId IS NULL) AND " +
            "(bt.payerFsp LIKE :payerFsp OR bt.payerFsp IS NULL)")
    Long getTotalApprovedAmount(String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT SUM(bt.approvedAmount) FROM Batch bt " +
            "WHERE bt.startedAt >= :dateFrom AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId or bt.registeringInstitutionId IS NULL) AND " +
            "(bt.payerFsp LIKE :payerFsp or bt.payerFsp IS NULL)")
    Long getTotalApprovedAmountDateFrom(Date dateFrom, String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT SUM(bt.approvedAmount) FROM Batch bt " +
            "WHERE bt.startedAt <= :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId or bt.registeringInstitutionId IS NULL) AND " +
            "(bt.payerFsp LIKE :payerFsp or bt.payerFsp IS NULL)")
    Long getTotalApprovedAmountDateTo(Date dateTo, String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT SUM(bt.approvedAmount) FROM Batch bt " +
            "WHERE bt.startedAt BETWEEN :dateFrom AND :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId or bt.registeringInstitutionId IS NULL) AND " +
            "(bt.payerFsp LIKE :payerFsp or bt.payerFsp IS NULL)")
    Long getTotalApprovedAmountDateBetween(Date dateFrom, Date dateTo, String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT SUM(bt.approvedCount) FROM Batch bt " +
            "WHERE (bt.registeringInstitutionId LIKE :registeringInstitutionId or bt.registeringInstitutionId IS NULL) AND " +
            "(bt.payerFsp LIKE :payerFsp OR bt.payerFsp IS NULL)")
    Long getTotalApprovedCount(String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT SUM(bt.approvedCount) FROM Batch bt " +
            "WHERE bt.startedAt >= :dateFrom AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId or bt.registeringInstitutionId IS NULL) AND " +
            "(bt.payerFsp LIKE :payerFsp or bt.payerFsp IS NULL)")
    Long getTotalApprovedCountDateFrom(Date dateFrom, String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT SUM(bt.approvedCount) FROM Batch bt " +
            "WHERE bt.startedAt <= :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId or bt.registeringInstitutionId IS NULL) AND " +
            "(bt.payerFsp LIKE :payerFsp or bt.payerFsp IS NULL)")
    Long getTotalApprovedCountDateTo(Date dateTo, String registeringInstitutionId, String payerFsp);

    @Query(value = "SELECT SUM(bt.approvedCount) FROM Batch bt " +
            "WHERE bt.startedAt BETWEEN :dateFrom AND :dateTo AND " +
            "(bt.registeringInstitutionId LIKE :registeringInstitutionId or bt.registeringInstitutionId IS NULL) AND " +
            "(bt.payerFsp LIKE :payerFsp or bt.payerFsp IS NULL)")
    Long getTotalApprovedCountDateBetween(Date dateFrom, Date dateTo, String registeringInstitutionId, String payerFsp);

}

package org.apache.fineract.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.fineract.operations.Batch;
import org.apache.fineract.operations.BatchPaginatedResponse;
import org.apache.fineract.operations.BatchRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.List;
import static org.apache.fineract.core.service.OperatorUtils.dateFormat;

@Slf4j
@Service
public class BatchDbServiceImpl implements BatchDbService {

    private final BatchRepository batchRepository;

    public BatchDbServiceImpl(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }

    @Override
    public BatchPaginatedResponse getBatch(String startFrom, String startTo, String registeringInstitutionId, String payerFsp, String batchId, PageRequest pager) {
        try {
            Long totalTransactions = batchRepository.getTotalTransactionsDateBetween(
                    dateFormat().parse(startFrom), dateFormat().parse(startTo),
                    registeringInstitutionId, payerFsp, batchId);
            Long totalAmount = batchRepository.getTotalAmountDateBetween(
                    dateFormat().parse(startFrom), dateFormat().parse(startTo),
                    registeringInstitutionId, payerFsp, batchId);
            Long totalBatches = batchRepository.getTotalBatchesDateBetween(
                    dateFormat().parse(startFrom), dateFormat().parse(startTo),
                    registeringInstitutionId, payerFsp, batchId);
            Long totalApprovedCount = batchRepository.getTotalApprovedCountDateBetween(
                    dateFormat().parse(startFrom), dateFormat().parse(startTo),
                    registeringInstitutionId, payerFsp, batchId);
            Long totalApprovedAmount = batchRepository.getTotalApprovedAmountDateBetween(
                    dateFormat().parse(startFrom), dateFormat().parse(startTo),
                    registeringInstitutionId, payerFsp, batchId);
            List<Batch> batches = batchRepository.findAllFilterDateBetween(
                    dateFormat().parse(startFrom), dateFormat().parse(startTo),
                    registeringInstitutionId, payerFsp, batchId,
                    pager);
            return getBatchPaginatedResponseInstance(totalBatches, totalTransactions, totalAmount, totalApprovedCount,
                    totalApprovedAmount, 10, batches);
        } catch (Exception e) {
            log.warn("failed to parse dates {} / {}", startFrom, startTo);
            return null;
        }
    }

    @Override
    public BatchPaginatedResponse getBatch(String registeringInstitutionId, String payerFsp, String batchId, PageRequest pager) {
        Long totalTransactions = batchRepository.getTotalTransactions(registeringInstitutionId, payerFsp, batchId);
        Long totalAmount = batchRepository.getTotalAmount(registeringInstitutionId, payerFsp, batchId);
        Long totalBatches = batchRepository.getTotalBatches(registeringInstitutionId, payerFsp, batchId);
        Long totalApprovedCount = batchRepository.getTotalApprovedCount(registeringInstitutionId, payerFsp, batchId);
        Long totalApprovedAmount = batchRepository.getTotalApprovedAmount(registeringInstitutionId, payerFsp, batchId);
        List<Batch> batches = batchRepository.findAllBatch(registeringInstitutionId, payerFsp, batchId, pager);
        return getBatchPaginatedResponseInstance(totalBatches, totalTransactions, totalAmount, totalApprovedCount,
                totalApprovedAmount, 10, batches);
    }

    @Override
    public BatchPaginatedResponse getBatchDateTo(String startTo, String registeringInstitutionId, String payerFsp, String batchId, PageRequest pager) {
        try {
            Long totalTransactions = batchRepository.getTotalTransactionsDateTo(
                    dateFormat().parse(startTo),
                    registeringInstitutionId, payerFsp, batchId);
            Long totalAmount = batchRepository.getTotalAmountDateTo(
                    dateFormat().parse(startTo),
                    registeringInstitutionId, payerFsp, batchId);
            Long totalBatches = batchRepository.getTotalBatchesDateTo(
                    dateFormat().parse(startTo),
                    registeringInstitutionId, payerFsp, batchId);
            Long totalApprovedCount = batchRepository.getTotalApprovedCountDateTo(
                    dateFormat().parse(startTo),
                    registeringInstitutionId, payerFsp, batchId);
            Long totalApprovedAmount= batchRepository.getTotalApprovedAmountDateTo(
                    dateFormat().parse(startTo),
                    registeringInstitutionId, payerFsp, batchId);
            List<Batch> batches = batchRepository.findAllFilterDateTo(
                    dateFormat().parse(startTo),
                    registeringInstitutionId, payerFsp, batchId, pager);
            return getBatchPaginatedResponseInstance(totalBatches, totalTransactions, totalAmount, totalApprovedCount,
                    totalApprovedAmount, 10, batches);
        } catch (Exception e) {
            log.warn("failed to parse startTo date {}", startTo);
            return null;
        }
    }

    @Override
    public BatchPaginatedResponse getBatchDateFrom(String startFrom, String registeringInstitutionId, String payerFsp, String batchId, PageRequest pager) {
        try {
            Long totalTransactions = batchRepository.getTotalTransactionsDateFrom(
                    dateFormat().parse(startFrom),
                    registeringInstitutionId, payerFsp, batchId);
            Long totalAmount = batchRepository.getTotalAmountDateFrom(
                    dateFormat().parse(startFrom),
                    registeringInstitutionId, payerFsp, batchId);
            Long totalBatches = batchRepository.getTotalBatchesDateFrom(
                    dateFormat().parse(startFrom),
                    registeringInstitutionId, payerFsp, batchId);
            Long totalApprovedCount = batchRepository.getTotalApprovedCountDateFrom(
                    dateFormat().parse(startFrom),
                    registeringInstitutionId, payerFsp, batchId);
            Long totalApprovedAmount = batchRepository.getTotalApprovedAmountDateFrom(
                    dateFormat().parse(startFrom),
                    registeringInstitutionId, payerFsp, batchId);
            List<Batch> batches = batchRepository.findAllFilterDateFrom(
                    dateFormat().parse(startFrom),
                    registeringInstitutionId, payerFsp, batchId, pager);
            return getBatchPaginatedResponseInstance(totalBatches, totalTransactions, totalAmount, totalApprovedCount,
                    totalApprovedAmount, 10, batches);
        } catch (Exception e) {
            log.warn("failed to parse startFrom date {}", startFrom);
            return null;
        }
    }


    private BatchPaginatedResponse getBatchPaginatedResponseInstance(long totalBatches, long totalTransactions,
                                                                     long totalAmount, long totalApprovedCount,
                                                                     long totalApprovedAmount,
                                                                     long totalSubBatchesCreated, List<Batch> batches) {
        log.info("TotalBatch: {}, TotalTransactions: {}, Total Amount: {}, Batches: {}",
                totalBatches, totalTransactions, totalAmount, batches.size());
        BatchPaginatedResponse batchPaginatedResponse = new BatchPaginatedResponse();
        batchPaginatedResponse.setData(batches);
        batchPaginatedResponse.setTotalBatches(totalBatches);
        batchPaginatedResponse.setTotalTransactions(totalTransactions);
        batchPaginatedResponse.setTotalAmount(totalAmount);
        batchPaginatedResponse.setTotalApprovedCount(totalApprovedCount);
        batchPaginatedResponse.setTotalApprovedAmount(totalApprovedAmount);
        batchPaginatedResponse.setTotalSubBatchesCreated(totalSubBatchesCreated);
        return batchPaginatedResponse;
    }
}

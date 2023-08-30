package org.apache.fineract.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.fineract.operations.Batch;
import org.apache.fineract.operations.BatchPaginatedResponse;
import org.apache.fineract.operations.BatchRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
            Date startDateObject = dateFormat().parse(startFrom);
            Date endDateObject = dateFormat().parse(startTo);

            CompletableFuture<Optional<Long>> totalTransactionsAsync =
                    CompletableFuture.supplyAsync(() -> batchRepository.getTotalTransactionsDateBetween(
                    startDateObject, endDateObject,
                    registeringInstitutionId, payerFsp, batchId));
            CompletableFuture<Optional<Long>> totalAmountAsync =
                    CompletableFuture.supplyAsync(() -> batchRepository.getTotalAmountDateBetween(
                            startDateObject, endDateObject,
                            registeringInstitutionId, payerFsp, batchId));
            CompletableFuture<Optional<Long>> totalBatchesAsync =
                    CompletableFuture.supplyAsync(() -> batchRepository.getTotalBatchesDateBetween(
                            startDateObject, endDateObject,
                            registeringInstitutionId, payerFsp, batchId));
            CompletableFuture<Optional<Long>> totalApprovedCountAsync =
                    CompletableFuture.supplyAsync(() -> batchRepository.getTotalApprovedCountDateBetween(
                            startDateObject, endDateObject,
                            registeringInstitutionId, payerFsp, batchId));
            CompletableFuture<Optional<Long>> totalApprovedAmountAsync =
                    CompletableFuture.supplyAsync(() -> batchRepository.getTotalApprovedAmountDateBetween(
                            startDateObject, endDateObject,
                            registeringInstitutionId, payerFsp, batchId));
            CompletableFuture<List<Batch>> batchesAsync =
                    CompletableFuture.supplyAsync(() -> batchRepository.findAllFilterDateBetween(
                            startDateObject, endDateObject,
                            registeringInstitutionId, payerFsp, batchId,
                            pager));

            CompletableFuture<Void> allTasks = CompletableFuture.allOf(totalTransactionsAsync, totalAmountAsync,
                    totalBatchesAsync, totalApprovedCountAsync, totalApprovedAmountAsync, batchesAsync);
            allTasks.join();

            Optional<Long> totalTransactions = totalTransactionsAsync.join();
            Optional<Long> totalAmount = totalAmountAsync.join();
            Optional<Long> totalBatches = totalBatchesAsync.join();
            Optional<Long> totalApprovedCount = totalApprovedCountAsync.join();
            Optional<Long> totalApprovedAmount = totalApprovedAmountAsync.join();
            List<Batch> batches = batchesAsync.join();

            return getBatchPaginatedResponseInstance(totalBatches.orElse(0L), totalTransactions.orElse(0L),
                    totalAmount.orElse(0L), totalApprovedCount.orElse(0L),
                    totalApprovedAmount.orElse(0L), 10, batches);
        } catch (Exception e) {
            log.warn("failed to parse dates {} / {}", startFrom, startTo);
            return null;
        }
    }

    @Override
    public BatchPaginatedResponse getBatch(String registeringInstitutionId, String payerFsp, String batchId, PageRequest pager) {
        log.info("Get batch function");
        Optional<Long> totalTransactions = batchRepository.getTotalTransactions(registeringInstitutionId, payerFsp, batchId);
        Optional<Long> totalAmount = batchRepository.getTotalAmount(registeringInstitutionId, payerFsp, batchId);
        Optional<Long> totalBatches = batchRepository.getTotalBatches(registeringInstitutionId, payerFsp, batchId);
        Optional<Long> totalApprovedCount = batchRepository.getTotalApprovedCount(registeringInstitutionId, payerFsp, batchId);
        Optional<Long> totalApprovedAmount = batchRepository.getTotalApprovedAmount(registeringInstitutionId, payerFsp, batchId);
        List<Batch> batches = batchRepository.findAllBatch(registeringInstitutionId, payerFsp, batchId, pager);
        return getBatchPaginatedResponseInstance(totalBatches.orElse(0L), totalTransactions.orElse(0L),
                totalAmount.orElse(0L), totalApprovedCount.orElse(0L),
                totalApprovedAmount.orElse(0L), 10, batches);
    }

    @Override
    public BatchPaginatedResponse getBatchDateTo(String startTo, String registeringInstitutionId, String payerFsp, String batchId, PageRequest pager) {
        try {
            Optional<Long> totalTransactions = batchRepository.getTotalTransactionsDateTo(
                    dateFormat().parse(startTo),
                    registeringInstitutionId, payerFsp, batchId);
            Optional<Long> totalAmount = batchRepository.getTotalAmountDateTo(
                    dateFormat().parse(startTo),
                    registeringInstitutionId, payerFsp, batchId);
            Optional<Long> totalBatches = batchRepository.getTotalBatchesDateTo(
                    dateFormat().parse(startTo),
                    registeringInstitutionId, payerFsp, batchId);
            Optional<Long> totalApprovedCount = batchRepository.getTotalApprovedCountDateTo(
                    dateFormat().parse(startTo),
                    registeringInstitutionId, payerFsp, batchId);
            Optional<Long> totalApprovedAmount= batchRepository.getTotalApprovedAmountDateTo(
                    dateFormat().parse(startTo),
                    registeringInstitutionId, payerFsp, batchId);
            List<Batch> batches = batchRepository.findAllFilterDateTo(
                    dateFormat().parse(startTo),
                    registeringInstitutionId, payerFsp, batchId, pager);
            return getBatchPaginatedResponseInstance(totalBatches.orElse(0L), totalTransactions.orElse(0L),
                    totalAmount.orElse(0L), totalApprovedCount.orElse(0L),
                    totalApprovedAmount.orElse(0L), 10, batches);
        } catch (Exception e) {
            log.warn("failed to parse startTo date {}", startTo);
            return null;
        }
    }

    @Override
    public BatchPaginatedResponse getBatchDateFrom(String startFrom, String registeringInstitutionId, String payerFsp, String batchId, PageRequest pager) {
        try {
            Optional<Long> totalTransactions = batchRepository.getTotalTransactionsDateFrom(
                    dateFormat().parse(startFrom),
                    registeringInstitutionId, payerFsp, batchId);
            Optional<Long> totalAmount = batchRepository.getTotalAmountDateFrom(
                    dateFormat().parse(startFrom),
                    registeringInstitutionId, payerFsp, batchId);
            Optional<Long> totalBatches = batchRepository.getTotalBatchesDateFrom(
                    dateFormat().parse(startFrom),
                    registeringInstitutionId, payerFsp, batchId);
            Optional<Long> totalApprovedCount = batchRepository.getTotalApprovedCountDateFrom(
                    dateFormat().parse(startFrom),
                    registeringInstitutionId, payerFsp, batchId);
            Optional<Long> totalApprovedAmount = batchRepository.getTotalApprovedAmountDateFrom(
                    dateFormat().parse(startFrom),
                    registeringInstitutionId, payerFsp, batchId);
            List<Batch> batches = batchRepository.findAllFilterDateFrom(
                    dateFormat().parse(startFrom),
                    registeringInstitutionId, payerFsp, batchId, pager);
            return getBatchPaginatedResponseInstance(totalBatches.orElse(0L), totalTransactions.orElse(0L),
                    totalAmount.orElse(0L), totalApprovedCount.orElse(0L),
                    totalApprovedAmount.orElse(0L), 10, batches);
        } catch (Exception e) {
            log.warn("failed to parse startFrom date {}", startFrom);
            return null;
        }
    }


    private BatchPaginatedResponse getBatchPaginatedResponseInstance(long totalBatches, long totalTransactions,
                                                                     long totalAmount, long totalApprovedCount,
                                                                     long totalApprovedAmount,
                                                                     long totalSubBatchesCreated, List<Batch> batches) {
        log.info("Inside getBatchPaginatedResponseInstance");
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

package org.apache.fineract.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.fineract.operations.*;
import org.apache.fineract.response.BatchAndSubBatchSummaryResponse;
import org.apache.fineract.response.SubBatchSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class BatchServiceImpl implements BatchService{
    
    @Autowired
    private BatchRepository batchRepository;
    @Autowired
    private TransferRepository transferRepository;
    private static long subBatchAmount;
    private static long subBatchCount;
    private static Set<String> payeeFspSet;


    @Override
    public BatchAndSubBatchSummaryResponse getBatchAndSubBatchSummary(String batchId, String clientCorrelationId) {

        List<Batch> batchAndSubBatches = batchRepository.findAllByBatchId(batchId);

        if(CollectionUtils.isEmpty(batchAndSubBatches)){
            return null;
        }

        BatchAndSubBatchSummaryResponse response = new BatchAndSubBatchSummaryResponse();
        subBatchCount=0;
        subBatchAmount=0;
        Long totalSubBatch = 0L;

        for(Batch batch : batchAndSubBatches){
            if(StringUtils.isEmpty(batch.getSubBatchId())){
                updateResponseWithBatchInfo(batch, response);
            }
            else{
                SubBatchSummary subBatchSummary = updateResponseWithSubBatchInfo(batch, response);
                totalSubBatch++;
                response.getSubBatchSummaryList().add(subBatchSummary);
            }
        }
        response.setApprovedTransactionCount(subBatchCount);
        response.setApprovedAmount(subBatchAmount);
        response.setTotalSubBatches(totalSubBatch);

        return response;
    }

    @Override
    public PaymentBatchDetail getPaymentBathDetail(String batchId, String clientCorrelationId, int offset, int limit, String orderBy, String sortBy) {
        List<Batch> batchAndSubBatches = batchRepository.findAllByBatchId(batchId);
        //List<Batch> subBatchList = batchRepository.findAllSubBatchId(batchId);
        //Batch batch = batchRepository.findByBatchId(batchId);
        if (CollectionUtils.isEmpty(batchAndSubBatches)) {
            return null;
        }
        List<Instruction> allInstructions =  new ArrayList<>();
        PaymentBatchDetail response = new PaymentBatchDetail();
        subBatchCount = 0;
        subBatchAmount = 0;
        List<SubBatchSummary> subBatchSummaryList = new ArrayList<>();
        if (batchAndSubBatches.size() == 1) {
            //Batch batch = batchAndSubBatches.get(0);
            updatePaymentDetailBatchInfo(batchAndSubBatches.get(0), response);
            int pageNumber = (offset / limit) ;
            Page<Transfer> transferList =  transferRepository.findAllByBatchId(batchId, new PageRequest(pageNumber, limit));
            List<Instruction>  instructionList = generateInstructionList(transferList.getContent(),orderBy,sortBy);
            response.setInstructionList(instructionList);
            response.setTotalInstruction(batchAndSubBatches.get(0).getTotalTransactions());
            return response;
        }
        Long totalInstruction = 0L;
        for(Batch batch : batchAndSubBatches) {
            if (StringUtils.isEmpty(batch.getSubBatchId())) {
                updatePaymentDetailBatchInfo(batch, response);
            } else {
                payeeFspSet = new HashSet<>();
                int pageNumber = (offset / limit);
                Page<Transfer> transferList =  transferRepository.findAllByBatchId(batch.getSubBatchId(), new PageRequest(pageNumber, limit));

                log.info(transferList.toString());
                List<Instruction>  instructionList = generateInstructionList(transferList.getContent(),orderBy,sortBy);
                allInstructions.addAll(instructionList);

                SubBatchSummary subBatch = updateSubBatchPaymentDetail(batch, response);
                Long subBatchCount = transferRepository.countAllByBatchId(batch.getSubBatchId());
                totalInstruction += subBatchCount;
                subBatchSummaryList.add(subBatch);

            }
        }
/*        updatePaymentDetailBatchInfo(batch, response);
        for(Batch subBatches : subBatchList) {
            payeeFspSet = new HashSet<>();
            int pageNumber = (offset / limit);
            Page<Transfer> transferList =  transferRepository.findAllByBatchId(batch.getSubBatchId(), new PageRequest(pageNumber, limit));
            log.info(subBatches.getBatchId());
            log.info(transferList.toString());
            List<Instruction>  instructionList = generateInstructionList(transferList.getContent(),orderBy,sortBy);
            allInstructions.addAll(instructionList);

            SubBatchSummary subBatch = updateSubBatchPaymentDetail(batch, response);
            Long subBatchCount = transferRepository.countAllByBatchId(batch.getSubBatchId());
            totalInstruction += subBatchCount;
            subBatchSummaryList.add(subBatch);
        }*/
        response.setInstructionList(allInstructions);
        response.setTotalInstruction(subBatchCount);
        response.setSubBatchList(subBatchSummaryList);
        return response;
    }
    public List<Instruction> generateInstructionList(List<Transfer> transferList, String orderBy, String sortBy){
        List<Instruction> instructionList = new ArrayList<>();

        for (Transfer transfer : transferList) {
            log.info(String.valueOf(transfer));
            Instruction instruction = new Instruction();
            instruction.setInstructionId(transfer.getTransactionId());
            instruction.setPayerFsp(transfer.getPayerDfspId() != null ? transfer.getPayerDfspId() : null);
            instruction.setPayeeFunctionalId(transfer.getPayeePartyId() != null ? transfer.getPayeePartyId() : null);
            instruction.setAmount(transfer.getAmount() != null ? transfer.getAmount() : BigDecimal.valueOf(0));
            instruction.setStatus(transfer.getStatus() != null ? transfer.getStatus() : null);
            instruction.setStartedAt(transfer.getStartedAt() != null ? transfer.getStartedAt() : null);
            instruction.setCompletedAt(transfer.getCompletedAt() != null ? transfer.getCompletedAt() : null);
            instruction.setSubBatchId(transfer.getBatchId() != null ? transfer.getBatchId() : null);
            if (transfer.getPayeeDfspId() != null) {
                payeeFspSet.add(transfer.getPayeeDfspId());
            }

            instructionList.add(instruction);
        }
        Comparator<Instruction> comparator = null;
        Boolean validOrderBy = true;

        if ("instructionId".equals(orderBy)) {
            comparator = Comparator.comparing(Instruction::getPayerFsp);
        } else if ("payeeFunctionalId".equals(orderBy)) {
            comparator = Comparator.comparing(Instruction::getPayeeFunctionalId);
        } else if ("subBatchId".equals(orderBy)) {
            comparator = Comparator.comparing(Instruction::getAmount);
        } else {
            validOrderBy=false;
        }

        if ("desc".equalsIgnoreCase(sortBy)  && validOrderBy && comparator != null) {
            comparator = comparator.reversed();
        }
        return  instructionList;
    }

    @Override
    public SubBatchSummary getPaymentSubBatchDetail(String batchId, String subBatchId, String clientCorrelationId, int offset, int limit, String orderBy, String sortBy) {
        Batch batch = batchRepository.findBySubBatchId(subBatchId);
        SubBatchSummary subBatch = new SubBatchSummary();
        subBatch.setSubBatchId(subBatchId);
        subBatch.setPayerFsp(batch.getPayerFsp() != null ? batch.getPayerFsp() : null);
        subBatch.setGeneratedAt(LocalDateTime.now().toString());
        subBatch.setBatchId(batchId);

        Long totalInstructionCount = transferRepository.countAllByBatchId(subBatchId);
        //List<Transfer> transferList = transferRepository.findAllByBatchId(subBatchId);
        int pageNumber = (offset / limit) ;
        List<Transfer> transferList =  transferRepository.findAllByBatchId(subBatchId);


        List<Instruction> instructionList = new ArrayList<>();

        for (Transfer transfer : transferList) {
            Instruction instruction = new Instruction();
            instruction.setInstructionId(transfer.getTransactionId());
            instruction.setPayeeFunctionalId(transfer.getPayeePartyId() != null ? transfer.getPayerPartyId() : null);
            instruction.setPayerFsp(transfer.getPayerDfspId() != null ? transfer.getPayerDfspId() : null);
            instruction.setAmount(transfer.getAmount() != null ? transfer.getAmount() : null);
            instruction.setStatus(transfer.getStatus() != null ? transfer.getStatus() : null);
            instruction.setReason(transfer.getStatusDetail() != null ? transfer.getStatusDetail() : null);
            instruction.setStartedAt(transfer.getStartedAt() != null ? transfer.getStartedAt() : null);
            instruction.setCompletedAt(transfer.getCompletedAt() != null ? transfer.getCompletedAt() : null);
            instructionList.add(instruction);
        }
        Comparator<Instruction> comparator = null;
        Boolean validOrderBy = true;

        if ("instructionId".equals(orderBy)) {
            comparator = Comparator.comparing(Instruction::getPayerFsp);
        } else if ("payeeFunctionalId".equals(orderBy)) {
            comparator = Comparator.comparing(Instruction::getPayeeFunctionalId);
        } else if ("subBatchId".equals(orderBy)) {
            comparator = Comparator.comparing(Instruction::getAmount);
        } else {
            validOrderBy=false;
        }

        if ("desc".equalsIgnoreCase(sortBy)  && validOrderBy && comparator != null) {
            comparator = comparator.reversed();
        }

        subBatch.setInstructionList(instructionList);
        subBatch.setTotalInstructionCount(totalInstructionCount);
        subBatch.setTotalAmount(subBatch.getTotalAmount());

        return subBatch;
    }

    private void updatePaymentDetailBatchInfo( Batch batch, PaymentBatchDetail response){
        log.info("Inside batch");
        response.setBatchId(batch.getBatchId());
        response.setPayerFsp(batch.getPayerFsp());
        response.setTotalBatchAmount(batch.getTotalAmount());
        response.setReportGeneratedAt(LocalDateTime.now().toString());
        response.setStartedAt(batch.getStartedAt());
        response.setCompletedAt(batch.getCompletedAt());
        response.setRegisteringInstitutionId(batch.getRegisteringInstitutionId());
        response.setStatus(String.valueOf(batch.getStatus()));
        response.setClientCorrelationId(batch.getCorrelationId());
        response.setFailed(batch.getFailed());
        response.setOngoing(batch.getOngoing());
        response.setSuccessful(batch.getCompleted());
        response.setTotal(batch.getTotalTransactions());
        response.setTotalAmount(BigDecimal.valueOf(batch.getTotalAmount()));
        response.setSuccessfulAmount(BigDecimal.valueOf(batch.getCompletedAmount()));
        response.setFailedAmount(BigDecimal.valueOf(batch.getFailedAmount()));
        response.setPendingAmount(BigDecimal.valueOf(batch.getOngoingAmount()));
    }
    private SubBatchSummary updateSubBatchPaymentDetail(Batch batch, PaymentBatchDetail response){
        SubBatchSummary subBatch = new SubBatchSummary();
        subBatch.setSubBatchId(batch.getSubBatchId());
        subBatch.setPayerFsp(batch.getPayerFsp());
        List<Transfer> transferList = transferRepository.findAllByBatchId(batch.getSubBatchId());
        if(!transferList.isEmpty()) {
            subBatch.setBudgetAccount(transferList.get(0).getPayerPartyId() != null ? transferList.get(0).getPayerPartyId() : null);
        }
        subBatch.setTotalAmount(batch.getTotalAmount()!=null? BigDecimal.valueOf(batch.getTotalAmount()): BigDecimal.valueOf(0));
        subBatch.setTotal(batch.getTotalTransactions()!=null ? batch.getTotalTransactions(): 0 );
        subBatch.setPayeeFspSet(payeeFspSet);
        return subBatch;
    }

    private SubBatchSummary updateResponseWithSubBatchInfo(Batch batch, BatchAndSubBatchSummaryResponse response) {
        double batchFailedPercent = 0;
        double batchSuccessPercent = 0;

        if (batch != null) {
            if (CollectionUtils.isEmpty(response.getSubBatchSummaryList())) {
                response.setSubBatchSummaryList(new ArrayList<>());
            }

            if (batch.getTotalTransactions() != null) {
                batchFailedPercent = ((double) batch.getFailed()) / batch.getTotalTransactions() * 100;
                batchSuccessPercent = ((double) batch.getCompleted()) / batch.getTotalTransactions() * 100;
            }

            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            decimalFormat.setRoundingMode(RoundingMode.FLOOR);

            SubBatchSummary subBatchSummary = new SubBatchSummary();
            subBatchSummary.setBatchId(batch.getBatchId());
            subBatchSummary.setSubBatchId(batch.getSubBatchId());
            subBatchSummary.setRequestId(batch.getRequestId());

            subBatchSummary.setTotal(batch.getTotalTransactions() != null ? batch.getTotalTransactions() : 0);
            subBatchSummary.setTotalAmount(BigDecimal.valueOf(batch.getTotalAmount() != null ? batch.getTotalAmount() : 0));
            subBatchSummary.setOngoing(batch.getOngoing() != null ? batch.getOngoing() : 0);
            subBatchSummary.setPendingAmount(BigDecimal.valueOf(batch.getOngoingAmount() != null ? batch.getOngoingAmount() : 0));
            subBatchSummary.setSuccessful(batch.getCompleted() != null ? batch.getCompleted() : 0);
            subBatchSummary.setSuccessfulAmount(BigDecimal.valueOf(batch.getCompletedAmount() != null ? batch.getCompletedAmount() : 0));
            subBatchSummary.setFailed(batch.getFailed() != null ? batch.getFailed() : 0);
            subBatchSummary.setFailedAmount(BigDecimal.valueOf(batch.getFailedAmount() != null ? batch.getFailedAmount() : 0));
            subBatchSummary.setFile(batch.getRequestFile());
            subBatchSummary.setNotes(batch.getNote());
            subBatchSummary.setCreatedAt(batch.getStartedAt() != null ? batch.getStartedAt().toString() : null);

            subBatchSummary.setModes(batch.getPaymentMode());
            subBatchSummary.setPurpose(null);
            subBatchSummary.setSuccessPercentage(decimalFormat.format(batchSuccessPercent));
            subBatchSummary.setFailedPercentage(decimalFormat.format(batchFailedPercent));
            subBatchSummary.setApprovedAmount(batch.getApprovedAmount());
            subBatchSummary.setApprovedTransactionCount(batch.getApprovedCount());
            subBatchSummary.setPayerFsp(batch.getPayerFsp());
            subBatchAmount += batch.getApprovedAmount() != null ? batch.getApprovedAmount() : 0;
            subBatchCount += batch.getApprovedCount() != null ? batch.getApprovedCount() : 0;
            List<Transfer> transferList =  transferRepository.findAllByBatchId(batch.getSubBatchId());
            Set<String> payeeFspSet = new HashSet<>();
            for (Transfer transfer : transferList) {
                payeeFspSet.add(transfer.getPayeeDfspId());
            }
            subBatchSummary.setPayeeFspSet(payeeFspSet);
            subBatchSummary.setStartedAt(batch.getStartedAt());
            subBatchSummary.setCompletedAt(batch.getCompletedAt());
            subBatchSummary.setStatus(String.valueOf(batch.getStatus()));


            return subBatchSummary;
        } else {
            return null; // Return null if batch is null
        }
    }
    private void updateResponseWithBatchInfo(Batch batch, BatchAndSubBatchSummaryResponse response) {
        double batchFailedPercent = 0;
        double batchSuccessPercent = 0;

        if (batch != null) {
            if (batch.getTotalTransactions() != null) {
                batchFailedPercent = ((double) batch.getFailed()) / batch.getTotalTransactions() * 100;
                batchSuccessPercent = ((double) batch.getCompleted()) / batch.getTotalTransactions() * 100;
            }

            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            decimalFormat.setRoundingMode(RoundingMode.FLOOR);

            response.setBatchId(batch.getBatchId());
            response.setRequestId(batch.getRequestId());

            response.setTotal(batch.getTotalTransactions() != null ? batch.getTotalTransactions() : 0);
            response.setTotalAmount(BigDecimal.valueOf(batch.getTotalAmount() != null ? batch.getTotalAmount() : 0));
            response.setOngoing(batch.getOngoing() != null ? batch.getOngoing() : 0);
            response.setPendingAmount(BigDecimal.valueOf(batch.getOngoingAmount() != null ? batch.getOngoingAmount() :0));
            response.setSuccessful(batch.getCompleted() != null ? batch.getCompleted() : 0);
            response.setSuccessfulAmount(BigDecimal.valueOf(batch.getCompletedAmount() !=null ? batch.getCompletedAmount(): 0));
            response.setFailed(batch.getFailed() != null ? batch.getFailed() : 0);
            response.setFailedAmount(BigDecimal.valueOf(batch.getFailedAmount()!= null ? batch.getFailedAmount() : 0));
            response.setFile(batch.getResult_file());
            response.setNotes(batch.getNote());

            response.setCreatedAt(batch.getStartedAt() != null ? batch.getStartedAt().toString() : null);

            response.setModes(batch.getPaymentMode());
            response.setPurpose(null);
            response.setSuccessPercentage(decimalFormat.format(batchSuccessPercent));
            response.setFailedPercentage(decimalFormat.format(batchFailedPercent));
            response.setApprovedTransactionCount(batch.getApprovedCount() != null ? batch.getApprovedCount() : 0);
            response.setApprovedAmount(batch.getApprovedAmount()!= null ? batch.getApprovedAmount() : 0);
            response.setPayerFsp(batch.getPayerFsp()!= null ? batch.getPayerFsp(): null);
            response.setGeneratedAt(LocalDateTime.now().toString());
            response.setTotalInstructionCount(transferRepository.countAllByBatchId(batch.getBatchId()));
            response.setStartedAt(batch.getStartedAt());
            response.setCompletedAt(batch.getCompletedAt());
            response.setRegisteringInstitutionId(batch.getRegisteringInstitutionId());
            response.setStatus(String.valueOf(batch.getStatus()));


        }
    }
}

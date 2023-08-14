package org.apache.fineract.service.batch;

import org.apache.fineract.operations.Batch;
import org.apache.fineract.operations.BatchRepository;
import org.apache.fineract.operations.Transfer;
import org.apache.fineract.operations.TransferRepository;
import org.apache.fineract.response.BatchWithSubBatchesAndTransactionsDetailResponse;
import org.apache.fineract.response.SubBatchDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class BatchServiceImpl implements BatchService{

    @Autowired
    private TransferRepository transferRepository;

    @Autowired
    private BatchRepository batchRepository;

    @Override
    public BatchWithSubBatchesAndTransactionsDetailResponse getBatchWithSubBatchesAndTransactionsDetail
            (String batchId, String clientCorrelationId, int pageNo, int pageSize, String sortBy, String orderBy) {
        // fetch from transfers table based on group by sub batch id and order by started at date
        Sort sort = Sort.by(createSortOrder("subBatchId", "asc"), createSortOrder(sortBy, orderBy));
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, sort);
        Page<Transfer> page = transferRepository.findAllByBatchId(batchId, pageRequest);
        BatchWithSubBatchesAndTransactionsDetailResponse response = new BatchWithSubBatchesAndTransactionsDetailResponse();

        List<Transfer> transferList = page.getContent();
        List<SubBatchDetail> subBatchDetailList = generateSubBatchesAndTransactionsDetail(transferList);
        response.setSubBatchDetails(subBatchDetailList);

        // fetch batch from batches repo using batchId and subBatchId is null
        Batch batch = batchRepository.findOneByBatchIdAndSubBatchIdIsNull(batchId);
        response.setBatchId(batch.getBatchId());
        response.setTotalAmount(batch.getTotalAmount());
        response.setTotalTransactionCount(batch.getTotalTransactions());

        // set this values
//        response.setGeneratedAt();
//        response.setGeneratedBy();
//        response.setPayerFsp();

        return response;
    }

    private void generateBatchDetail(Batch batch) {

    }

    private List<SubBatchDetail> generateSubBatchesAndTransactionsDetail(List<Transfer> transferList) {

//        List<SubBatchDetail> subBatchDetailList = new ArrayList<>();
        Map<String, SubBatchDetail> subBatchDetailMap = new LinkedHashMap<>();

        for (Transfer transfer : transferList){
            if(subBatchDetailMap.containsKey(transfer.getSubBatchId())){
                // add payee fsp to the list
                SubBatchDetail existingSubBatchDetail = subBatchDetailMap.get(transfer.getSubBatchId());
                existingSubBatchDetail.getPaginatedTransfers().add(transfer);
            }
            else{
                SubBatchDetail subBatchDetail = new SubBatchDetail();
                subBatchDetail.setSubBatchId(transfer.getSubBatchId());
                if(subBatchDetail.getPaginatedTransfers().isEmpty()){
                    List<Transfer> paginatedTransfers = new ArrayList<>();
                    paginatedTransfers.add(transfer);
                    subBatchDetail.setPaginatedTransfers(paginatedTransfers);
                }
                else{
                    subBatchDetail.getPaginatedTransfers().add(transfer);
                }

                // add below details as well
//                subBatchDetail.setTotalTransactionsInSubBatch();
//                subBatchDetail.setTotalAmountInSubBatch();
//                subBatchDetail.setPayeeFsp();
//                subBatchDetail.setPayerFsp();
//                subBatchDetail.setBudgetAccount();
                subBatchDetailMap.put(transfer.getSubBatchId(), new SubBatchDetail());

            }
        }
        return new ArrayList<>(subBatchDetailMap.values());
    }

    private List<String> fetchSubBatchIds(List<Transfer> transferList) {
        Set<String> subBatchIds = new HashSet<>();
        for(Transfer transfer : transferList){
            if (!StringUtils.isEmpty(transfer.getSubBatchId())){
                subBatchIds.add(transfer.getSubBatchId());
            }
        }
        return new ArrayList<>(subBatchIds);
    }

    private Sort.Order createSortOrder(String columnName, String sortOrder){
        if("desc".equalsIgnoreCase(sortOrder)){
            return Sort.Order.desc(columnName);
        }
        return Sort.Order.asc(columnName);
    }
}

package org.apache.fineract.service;

import org.apache.fineract.operations.Batch;
import org.apache.fineract.operations.BatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BatchService {

    @Autowired
    private BatchRepository batchRepository;

    public Batch findBatchUsingSubBatchId(String batchId, String subBatchId) {
        return batchRepository.findByBatchIdAndSubBatchId(batchId, subBatchId);
    }
}

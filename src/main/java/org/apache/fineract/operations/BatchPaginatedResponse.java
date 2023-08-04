package org.apache.fineract.operations;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class BatchPaginatedResponse {

    long totalBatches, totalTransactions, totalAmount;
    List<Batch> data;

}
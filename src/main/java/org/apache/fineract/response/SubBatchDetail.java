package org.apache.fineract.response;

import com.azure.core.annotation.Get;
import lombok.Getter;
import lombok.Setter;
import org.apache.fineract.operations.Transfer;

import java.util.List;

@Getter
@Setter
public class SubBatchDetail {

    String subBatchId;

    String payerFsp;

    String payeeFsp;

    String budgetAccount;

    Long totalTransactionsInSubBatch;

    Long totalAmountInSubBatch;

    List<Transfer> paginatedTransfers;

}

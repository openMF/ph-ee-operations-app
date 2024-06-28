package org.apache.fineract.recall.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface TransactionWritePlatformService {

    CommandProcessingResult recallTransaction(String transactionId, JsonCommand command);
}

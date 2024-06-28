package org.apache.fineract.recall.service;

import lombok.RequiredArgsConstructor;
import org.apache.fineract.core.service.CamundaService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.operations.Transfer;
import org.apache.fineract.operations.TransferRepository;
import org.apache.fineract.operations.Variable;
import org.apache.fineract.operations.VariableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class TransactionWritePlatformServiceImpl implements TransactionWritePlatformService {

    private final PlatformSecurityContext context;
    private CamundaService camundaService;
    private VariableRepository variableRepository;
    private TransferRepository transferRepository;

    @Override
    public CommandProcessingResult recallTransaction(String transactionId, JsonCommand command) {
        this.context.jwt();

        Transfer transfer = transferRepository.findFirstByTransactionIdAndDirection(transactionId, "OUTGOING");
        Optional<Variable> paymentSchemeOpt = variableRepository.findByWorkflowInstanceKeyAndVariableName("paymentScheme", transfer.getWorkflowInstanceKey());
        String paymentScheme = paymentSchemeOpt.orElseThrow(() -> new RuntimeException("Payment scheme not found for transactionId " + transactionId)).getValue();

        camundaService.startRecallFlow(command.json(), paymentScheme, transfer);
        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withTransactionId(transfer.getTransactionId()) //
                .build();
    }
}

package org.apache.fineract.operations;

import jakarta.persistence.Tuple;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

import static org.apache.fineract.core.service.OperatorUtils.formatDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransferDto {

    private Long workflowInstanceKey;
    private String transactionId;
    private String startedAt;
    private String completedAt;
    private String acceptanceDate;
    private TransferStatus status;
    private String statusDetail;
    private String payeeDfspId;
    private String payeePartyId;
    private String payeePartyIdType;
    private BigDecimal payeeFee;
    private String payeeFeeCurrency;
    private String payeeQuoteCode;
    private String payerDfspId;
    private String payerPartyId;
    private String payerPartyIdType;
    private BigDecimal payerFee;
    private String payerFeeCurrency;
    private String payerQuoteCode;
    private BigDecimal amount;
    private String currency;
    private String direction;
    private String errorInformation;
    private String batchId;
    private String endToEndIdentification;
    private String recallStatus;
    private String recallDirection;
    private String paymentStatus;
    private Integer recallCount;

    public TransferDto (Tuple tuple) {
        this.workflowInstanceKey = tuple.get(Transfer_.workflowInstanceKey.getName(), Long.class);
        this.startedAt = formatDate(tuple.get(Transfer_.startedAt.getName(), Date.class));
        this.completedAt = formatDate(tuple.get(Transfer_.completedAt.getName(), Date.class));
        this.acceptanceDate = formatDate(tuple.get(Transfer_.acceptanceDate.getName(), Date.class));
        this.transactionId = tuple.get(Transfer_.transactionId.getName(), String.class);
        this.payerPartyId = tuple.get(Transfer_.payerPartyId.getName(), String.class);
        this.payeePartyId = tuple.get(Transfer_.payeePartyId.getName(), String.class);
        this.payerDfspId = tuple.get(Transfer_.payerDfspId.getName(), String.class);
        this.payeeDfspId = tuple.get(Transfer_.payeeDfspId.getName(), String.class);
        this.amount = tuple.get(Transfer_.amount.getName(), BigDecimal.class);
        this.currency = tuple.get(Transfer_.currency.getName(), String.class);
        this.status = tuple.get(Transfer_.status.getName(), TransferStatus.class);
        this.recallStatus = tuple.get(Transfer_.recallStatus.getName(), String.class);
        this.recallDirection = tuple.get(Transfer_.recallDirection.getName(), String.class);
        if (tuple.getElements().stream().anyMatch(t -> Transfer_.recalls.getName().equals(t.getAlias()))) {
            this.recallCount = tuple.get(Transfer_.recalls.getName(), Long.class).intValue();
        }
    }
}
package org.apache.fineract.operations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.fineract.operations.TransferStatus;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransferDto {

    private Long workflowInstanceKey;
    private String transactionId;
    private Date startedAt;
    private Date completedAt;
    private Date acceptanceDate;
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

}
package org.apache.fineract.card.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.fineract.card.entity.CardStatus;
import org.apache.fineract.card.entity.Direction;
import org.apache.fineract.card.entity.PaymentScheme;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardTransactionDto {
    private String workflowInstanceKey;
    private CardStatus status;
    private String businessProcessStatus;
    private String transactionGroupId;
    private String transactionId;
    private Date transactionDateTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal amount;
    private String currency;
    private String transactionReference;
    private String requestId;
    private String maskedPan;
    private String cardHolderName;
    private String merchName;
    private String merchCountry;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal instructedAmount;
    private String instructedCurrency;
    private String transactionType;
    private String merchCategoryCode;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal feeAmount;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal holdAmount;
    private String token;
    private String cardAccountId;
    private String request;
    private String paymentTokenWallet;
    private PaymentScheme paymentScheme;
    private Direction direction;
    private String iban;
}
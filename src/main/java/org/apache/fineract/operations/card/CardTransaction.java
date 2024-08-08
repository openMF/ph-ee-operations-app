package org.apache.fineract.operations.card;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Cacheable(false)
@Table(name = "card_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardTransaction {

    @Id
    @Column(name = "workflow_instance_key")
    private String workflowInstanceKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "business_process_status")
    private BusinessProcessStatus businessProcessStatus;

    @Column(name = "transaction_group_id")
    private String transactionGroupId;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "transaction_date_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date transactionDateTime;

    @Column(name = "amount")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal amount;

    @Column(name = "currency")
    private String currency;

    @Column(name = "transaction_reference")
    private String transactionReference;

    @Column(name = "request_id")
    private String requestId;

    @Column(name = "masked_pan")
    private String maskedPan;

    @Column(name = "card_holder_name")
    private String cardHolderName;

    @Column(name = "merch_name")
    private String merchName;

    @Column(name = "merch_country")
    private String merchCountry;

    @Column(name = "instructed_amount")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal instructedAmount;

    @Column(name = "instructed_currency")
    private String instructedCurrency;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "merch_category_code")
    private String merchCategoryCode;

    @Column(name = "fee_amount")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal feeAmount;

    @Column(name = "hold_amount")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal holdAmount;

    @Column(name = "token")
    private String token;

    @Column(name = "card_account_id")
    private String cardAccountId;

    @Column(name = "request")
    private String request;

    @Column(name = "payment_token_wallet")
    private String paymentTokenWallet;

    @Column(name = "payment_scheme")
    private PaymentScheme paymentScheme;

    @Column(name = "direction")
    @Enumerated(EnumType.STRING)
    private Direction direction;

    @Column(name = "iban")
    private String iban;
}
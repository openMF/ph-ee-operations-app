package org.apache.fineract.operations;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "transfers")
@Data
public class Transfer {
    public enum TransferType {
        TRANSFER,
        RECALL,
        REQUEST_TO_PAY
    }


    @Id
    @Column(name = "WORKFLOW_INSTANCE_KEY")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long workflowInstanceKey;

    @Column(name = "TRANSACTION_ID")
    private String transactionId;

    @Column(name = "LAST_UPDATED")
    private Long lastUpdated;

    @Column(name = "STARTED_AT")
    private Date startedAt;
    @Column(name = "COMPLETED_AT")
    private Date completedAt;
    @Column(name = "ACCEPTANCE_DATE")
    private Date acceptanceDate;

    @Enumerated(EnumType.STRING)
    private TransferStatus status;

    @Column(name = "STATUS_DETAIL")
    private String statusDetail;

    @Column(name = "PAYEE_DFSP_ID")
    private String payeeDfspId;
    @Column(name = "PAYEE_PARTY_ID")
    private String payeePartyId;
    @Column(name = "PAYEE_PARTY_ID_TYPE")
    private String payeePartyIdType;
    @Column(name = "PAYEE_FEE")
    private BigDecimal payeeFee;
    @Column(name = "PAYEE_FEE_CURRENCY")
    private String payeeFeeCurrency;
    @Column(name = "PAYEE_QUOTE_CODE")
    private String payeeQuoteCode;

    @Column(name = "PAYER_DFSP_ID")
    private String payerDfspId;
    @Column(name = "PAYER_PARTY_ID")
    private String payerPartyId;
    @Column(name = "PAYER_PARTY_ID_TYPE")
    private String payerPartyIdType;
    @Column(name = "PAYER_FEE")
    private BigDecimal payerFee;
    @Column(name = "PAYER_FEE_CURRENCY")
    private String payerFeeCurrency;
    @Column(name = "PAYER_QUOTE_CODE")
    private String payerQuoteCode;

    @Column(name = "amount")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal amount;

    @Column(name = "currency")
    private String currency;

    @Column(name = "direction")
    private String direction;

    @Column(name = "rtp_direction")
    private String rtpDirection;

    @Column(name = "error_information")
    private String errorInformation;

    @Column(name = "BATCH_ID")
    private String batchId;

    @Column(name = "ENDTOENDIDENTIFICATION")
    private String endToEndIdentification;

    @Column(name = "RECALL_STATUS")
    private String recallStatus;

    @Column(name = "RECALL_DIRECTION")
    private String recallDirection;

    @Column(name = "BUSINESS_PROCESS_STATUS")
    private String businessProcessStatus;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transfer", fetch = FetchType.LAZY)
    private List<Variable> variables;

    @ManyToOne
    @JoinColumn(name = "TRANSACTION_ID", referencedColumnName = "TRANSACTION_ID", updatable = false, insertable = false)
    private Transfer parent;

    @OneToMany(mappedBy = "parent")
    private List<Transfer> recalls;

    public Transfer() {
    }

    public Transfer(Long workflowInstanceKey) {
        this.workflowInstanceKey = workflowInstanceKey;
        this.status = TransferStatus.IN_PROGRESS;
    }

}

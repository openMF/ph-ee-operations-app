package org.apache.fineract.operations;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "timestamps")
@Getter
@Setter
public class Timestamps {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "WORKFLOW_INSTANCE_KEY")
    private Long workflowInstanceKey;

    @Column(name = "TRANSACTION_ID")
    private String transactionId;

    @Column(name = "EXPORTED_TIME")
    private String exportedTime;

    @Column(name = "IMPORTED_TIME")
    private String importedTime;

    @Column(name = "ZEEBE_TIME")
    private String ZeebeTime;

}

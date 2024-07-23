package org.apache.fineract.operations;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Entity
@IdClass(VariableId.class)
@Table(name = "variables")
@Cacheable(false)
@Data
@With
@NoArgsConstructor
@AllArgsConstructor
public class Variable {

    @Id
    @Column(name = "WORKFLOW_INSTANCE_KEY")
    private Long workflowInstanceKey;

    @Id
    @Column(name = "NAME")
    private String name;

    @Column(name = "WORKFLOW_KEY")
    private Long workflowKey;

    @Column(name = "TIMESTAMP")
    private Long timestamp;

    @Column(name = "POSITION")
    private Long position;

    @Lob
    @Column(name = "VALUE")
    private String value;

    @ManyToOne
    private TransactionRequest transactionRequest;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WORKFLOW_INSTANCE_KEY", referencedColumnName = "WORKFLOW_INSTANCE_KEY", insertable = false, updatable = false)
    private Transfer transfer;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WORKFLOW_INSTANCE_KEY", referencedColumnName = "WORKFLOW_INSTANCE_KEY", insertable = false, updatable = false)
    private FileTransport fileTransport;
}
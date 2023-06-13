package org.apache.fineract.operations;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import jakarta.persistence.*;

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

    @Lob
    @Column(name = "VALUE")
    private String value;

    @ManyToOne
    private TransactionRequest transactionRequest;

}

package org.apache.fineract.operations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.checker.units.qual.A;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VariableDto {

    private Long workflowInstanceKey;
    private String name;
    private Long workflowKey;
    private Long timestamp;
    private String value;
    private TransactionRequest transactionRequest;

}

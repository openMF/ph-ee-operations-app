package org.apache.fineract.card.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.fineract.operations.TaskDto;
import org.apache.fineract.operations.VariableDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardTransactionDetail {
    private CardTransactionDto cardTransaction;
    private List<TaskDto> tasks;
    private List<VariableDto> variables;
}
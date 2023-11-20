package org.apache.fineract.operations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {

    private Long workflowKey;
    private Long workflowInstanceKey;
    private String timestamp;
    private String intent;
    private String recordType;
    private String type;
    private String elementId;
}

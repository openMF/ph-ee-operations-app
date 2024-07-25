package org.apache.fineract.operations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileTransportDetail {
    private FileTransportDto fileTransport;
    private List<TaskDto> tasks;
    private List<VariableDto> variables;
}
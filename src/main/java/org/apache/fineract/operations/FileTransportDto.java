package org.apache.fineract.operations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FileTransportDto {
    private Long workflowInstanceKey;
    private Long sessionNumber;
    private Date startedAt;
    private Date completedAt;
    private Date transactionDate;
    private String status;
    private String listOfBics;
    private FileTransport.TransportType transportType;
    private FileTransport.TransportDirection direction;
}
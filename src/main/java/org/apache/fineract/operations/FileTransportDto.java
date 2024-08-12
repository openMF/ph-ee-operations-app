package org.apache.fineract.operations;

import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class FileTransportDto {
    private Long workflowInstanceKey;
    private Long sessionNumber;
    private String startedAt;
    private String completedAt;
    private String transactionDate;
    private FileTransport.TransportStatus status;
    private String statusMessage;
    private String listOfBics;
    private FileTransport.TransportType transportType;
    private FileTransport.TransportDirection direction;
}
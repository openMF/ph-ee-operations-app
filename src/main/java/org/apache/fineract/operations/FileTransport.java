package org.apache.fineract.operations;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Cacheable(false)
@Table(name = "file_transport")
@NoArgsConstructor
@AllArgsConstructor
public class FileTransport {

    public enum TransportType {
        IG1,
        IG2
    }

    public enum TransportDirection {
        IN,
        OUT
    }

    @Id
    @Column(name = "WORKFLOW_INSTANCE_KEY")
    private Long workflowInstanceKey;

    @Column(name = "SESSION_NUMBER")
    private Long sessionNumber;

    @Column(name = "STARTED_AT")
    private Date startedAt;

    @Column(name = "COMPLETED_AT")
    private Date completedAt;

    @Column(name = "TRANSACTION_DATE")
    private Date transactionDate;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "LIST_OF_BICS")
    private String listOfBics;

    @Enumerated(EnumType.STRING)
    @Column(name = "TRANSPORT_TYPE")
    private TransportType transportType;

    @Enumerated(EnumType.STRING)
    @Column(name = "DIRECTION")
    private TransportDirection direction;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fileTransport", fetch = FetchType.LAZY)
    private List<Variable> variables;
}
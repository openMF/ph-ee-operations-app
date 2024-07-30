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

    public enum TransportStatus {
        COMPLETED,
        FAILED,
        IN_PROGRESS,
        EXCEPTION,
        UNKNOWN
    }

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
    @Temporal(TemporalType.TIMESTAMP)
    private Date startedAt;

    @Column(name = "COMPLETED_AT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date completedAt;

    @Column(name = "LAST_UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Long lastUpdated;

    @Column(name = "TRANSACTION_DATE")
    @Temporal(TemporalType.DATE)
    private Date transactionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private TransportStatus status;

    @Column(name = "STATUS_MESSAGE")
    private String statusMessage;

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
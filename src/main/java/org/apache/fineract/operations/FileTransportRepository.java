package org.apache.fineract.operations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;

import java.util.Date;

public interface FileTransportRepository extends JpaRepository<FileTransport, Long> {

    FileTransport findFirstByWorkflowInstanceKey(Long workflowInstanceKey);

    @Query(value = "select t.* from file_transport t where t.direction = ?1" +
            " and ((?2 is null) or (?2 is not null and t.status = ?2))" +
            " and ((?3 is null) or (?3 is not null and t.sessionNumber = ?3))" +
            " and (((?4 is null) or (?5 is null)) " +
            " or (?4 is not null and ?5 is not null and t.transactionDate between ?4 and ?5))",
            nativeQuery = true
    )
    Page<FileTransport> filteredQueryForUI(@Param("direction") FileTransport.TransportDirection direction,
                                           @Param("status") @Nullable FileTransport.TransportStatus status,
                                           @Param("sessionNumber") @Nullable Long sessionNumber,
                                           @Param("transactionDateFrom") @Nullable Date transactionDateFrom,
                                           @Param("transactionDateTo") @Nullable Date transactionDateTo,
                                           Pageable pageable);
}
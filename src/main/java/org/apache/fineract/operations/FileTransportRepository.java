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

    @Query("select t from FileTransport t where t.direction = :direction" +
            " and (coalesce(:status, null) is null or t.status = coalesce(:status, null))" +
            " and (coalesce(:sessionNumber, null) is null or t.sessionNumber = coalesce(:sessionNumber, null))" +
            " and ((coalesce(:transactionDateFrom, null) is null and coalesce(:transactionDateTo, null) is null) " +
            " or (t.transactionDate between coalesce(:transactionDateFrom, null) and coalesce(:transactionDateTo, null)))"
    )
    Page<FileTransport> findAllFiltered(@Param("direction") FileTransport.TransportDirection direction,
                                        @Param("status") @Nullable FileTransport.TransportStatus status,
                                        @Param("sessionNumber") @Nullable Long sessionNumber,
                                        @Param("transactionDateFrom") @Nullable Date transactionDateFrom,
                                        @Param("transactionDateTo") @Nullable Date transactionDateTo,
                                        Pageable pageable);
}
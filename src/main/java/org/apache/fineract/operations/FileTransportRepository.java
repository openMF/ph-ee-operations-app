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

    @Query("select t from FileTransport t where (:status is null or t.status = :status)" +
            " and (:sessionNumber is null or t.sessionNumber = :sessionNumber)" +
            " and ((cast(:transactionDateFrom as Date) is null and cast(:transactionDateTo as Date) is null) " +
            " or (t.transactionDate between cast(:transactionDateFrom as Date) and cast(:transactionDateTo as Date)))"
    )
    Page<FileTransport> findAllFiltered(@Param("status") @Nullable FileTransport.TransportStatus status,
                                        @Param("sessionNumber") @Nullable Long sessionNumber,
                                        @Param("transactionDateFrom") @Nullable Date transactionDateFrom,
                                        @Param("transactionDateTo") @Nullable Date transactionDateTo,
                                        Pageable pageable);
}
package org.apache.fineract.operations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TimestampRepository extends JpaRepository<Timestamps, String>, JpaSpecificationExecutor {

    @Query("SELECT t FROM Transfer t WHERE t.transactionId IN :transactionIds")
    List<Timestamps> findByTransactionIds(@Param("transactionIds") List<String> transactionIds);

}

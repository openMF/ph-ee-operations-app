package org.apache.fineract.reportapi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<ReportRequest, Long> {
    Optional<ReportRequest> findByReportName(String reportName);
}

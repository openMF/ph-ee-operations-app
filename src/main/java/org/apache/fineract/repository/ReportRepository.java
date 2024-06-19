package org.apache.fineract.repository;

import org.apache.fineract.reportrequest.ReportRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<ReportRequest, Long> {
}

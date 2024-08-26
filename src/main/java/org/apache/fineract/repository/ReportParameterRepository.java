package org.apache.fineract.repository;

import org.apache.fineract.reportrequest.ReportParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportParameterRepository extends JpaRepository<ReportParameter, Long> {
}

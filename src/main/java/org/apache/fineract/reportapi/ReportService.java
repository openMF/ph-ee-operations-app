package org.apache.fineract.reportapi;

import com.amazonaws.services.applicationautoscaling.model.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    private static final Pattern DATA_MODIFICATION_PATTERN =
            Pattern.compile("\\b(INSERT|UPDATE|DELETE|MERGE|ALTER|DROP|TRUNCATE|CREATE|GRANT|REVOKE)\\b", Pattern.CASE_INSENSITIVE);

    public List<ReportRequest> getAllReports() {
        return reportRepository.findAll();
    }

    public ReportRequest getReportById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new ReportNotFoundException("Report not found with id: " + id));
    }

    public ReportRequest createReport(@Valid ReportRequest reportRequest) {
        validateReportRequest(reportRequest);

        ReportRequest createdReport = reportRepository.save(reportRequest);
        return createdReport;
    }

    public ReportRequest updateReport(Long id, ReportRequest updatedReportRequest) {
        Optional<ReportRequest> optionalReport = reportRepository.findById(id);
        if (optionalReport.isPresent()) {
            ReportRequest existingReport = optionalReport.get();
            if (updatedReportRequest.getReportName() != null) {
                existingReport.setReportName(updatedReportRequest.getReportName());
            }
            if (updatedReportRequest.getReportParameters() != null) {
                existingReport.setReportParameters(updatedReportRequest.getReportParameters());
            }

            validateReportRequest(existingReport); // Validate updated report
            return reportRepository.save(existingReport);
        } else {
            throw new ReportNotFoundException("Report not found with id: " + id);
        }
    }

    public void deleteReport(Long id) {
        if (reportRepository.existsById(id)) {
            reportRepository.deleteById(id);
        } else {
            throw new ReportNotFoundException("Report not found with id: " + id);
        }
    }

    private void validateReportRequest(ReportRequest reportRequest) {
        if (reportRequest.getReportName() == null || reportRequest.getReportName().isEmpty()) {
            throw new ValidationException("Report name is required");
        }

        if (reportRequest.getReportSql() == null || reportRequest.getReportSql().isEmpty()) {
            throw new ValidationException("Report SQL query is required");
        }

        if (containsDataModification(reportRequest.getReportSql())) {
            throw new ValidationException("Report SQL query should not contain data modification statements (e.g., INSERT, UPDATE, DELETE)");
        }
    }

    private boolean containsDataModification(String sqlQuery) {
        return DATA_MODIFICATION_PATTERN.matcher(sqlQuery).find();
    }
}

package org.apache.fineract.ReportApi;

import com.amazonaws.services.applicationautoscaling.model.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    public List<ReportRequest> getAllReports() {
        return reportRepository.findAll();
    }

    public ReportRequest getReportById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new ReportNotFoundException("Report not found with id: " + id));
    }

    public ReportRequest createReport(@Valid ReportRequest reportRequest) {

        if (reportRequest.getReportName() == null || reportRequest.getReportName().isEmpty()) {
            throw new ValidationException("Report name is required");
        }


        ReportRequest createdReport = reportRepository.save(reportRequest);
        return createdReport;
    }


    public ReportRequest updateReport(Long id, ReportRequest updatedReportRequest) {
        Optional<ReportRequest> optionalReport = reportRepository.findById(id);
        if (optionalReport.isPresent()) {
            ReportRequest existingReport = optionalReport.get();
            existingReport.setReportName(updatedReportRequest.getReportName());
            existingReport.setReportType(updatedReportRequest.getReportType());
            existingReport.setReportSubType(updatedReportRequest.getReportSubType());
            existingReport.setReportCategory(updatedReportRequest.getReportCategory());
            existingReport.setDescription(updatedReportRequest.getDescription());
            existingReport.setReportSql(updatedReportRequest.getReportSql());
            existingReport.setReportParameters(updatedReportRequest.getReportParameters());
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


}

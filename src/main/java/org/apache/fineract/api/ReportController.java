package org.apache.fineract.api;

import com.amazonaws.services.applicationautoscaling.model.ValidationException;
import org.apache.fineract.reportrequest.ReportNotFoundException;
import org.apache.fineract.reportrequest.ReportRequest;
import org.apache.fineract.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/reports")
    public ResponseEntity<List<ReportRequest>> getAllReports() {
        List<ReportRequest> reports = reportService.getAllReports();
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/reports/{id}")
    public ResponseEntity<ReportRequest> getReportById(@PathVariable Long id) {
        try {
            ReportRequest report = reportService.getReportById(id);
            return ResponseEntity.ok(report);
        } catch (ReportNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/reports")
    public ResponseEntity<ReportRequest> createReport(@RequestHeader("Platform-TenantId") String tenantId,
                                                      @Valid @RequestBody ReportRequest reportRequest) {
        try {
            ReportRequest createdReport = reportService.createReport(reportRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdReport);
        } catch (ValidationException e) {

            return ResponseEntity.badRequest().build();
        }
    }


    @PutMapping("/reports/{id}")
    public ResponseEntity<ReportRequest> updateReport(@RequestHeader("Platform-TenantId") String tenantId,
                                                      @PathVariable Long id,
                                                      @RequestBody ReportRequest reportRequest) {
        try {
            ReportRequest updatedReport = reportService.updateReport(id, reportRequest);
            return ResponseEntity.ok(updatedReport);
        } catch (ReportNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/reports/{id}")
    public ResponseEntity<?> deleteReport(@PathVariable Long id) {
        try {
            reportService.deleteReport(id);
            return ResponseEntity.noContent().build();
        } catch (ReportNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

package org.apache.fineract.reportapi;

import com.amazonaws.services.applicationautoscaling.model.ValidationException;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reports")
@Tag(name = "Reports", description = "Endpoints for managing reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping
    public ResponseEntity<List<ReportRequest>> getAllReports(@RequestHeader("Platform-TenantId") String tenantId) {
        List<ReportRequest> reports = reportService.getAllReports();
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportRequest> getReportById(@PathVariable Long id) {
        try {
            ReportRequest report = reportService.getReportById(id);
            return ResponseEntity.ok(report);
        } catch (ReportNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping
    public ResponseEntity<?> createReport(@RequestHeader("Platform-TenantId") String tenantId,
                                          @Valid @RequestBody ReportRequest reportRequest) {
        try {
            ReportRequest createdReport = reportService.createReport(reportRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdReport);
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReport(@PathVariable Long id,
                                          @RequestHeader("Platform-TenantId") String tenantId,
                                          @RequestBody ReportRequest reportRequest) {
        try {
            ReportRequest updatedReport = reportService.updateReport(id, reportRequest);
            return ResponseEntity.ok(updatedReport);
        } catch (ReportNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReport(@PathVariable Long id, @RequestHeader("Platform-TenantId") String tenantId) {
        try {
            reportService.deleteReport(id);
            return ResponseEntity.noContent().build();
        } catch (ReportNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

package com.campusgig.backend.controller;

import com.campusgig.backend.entity.Report;
import com.campusgig.backend.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<Report> createReport(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> body) {
        Long reportedUserId = body.get("reportedUserId") != null
                ? ((Number) body.get("reportedUserId")).longValue() : null;
        Long jobId = body.get("jobId") != null
                ? ((Number) body.get("jobId")).longValue() : null;
        return ResponseEntity.ok(reportService.createReport(
                userDetails.getUsername(),
                reportedUserId,
                jobId,
                (String) body.get("reason"),
                (String) body.get("description")
        ));
    }

    @GetMapping
    public ResponseEntity<List<Report>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Report>> getPendingReports() {
        return ResponseEntity.ok(reportService.getPendingReports());
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<Report> resolveReport(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(reportService.resolveReport(
                id,
                (String) body.get("resolution"),
                Boolean.TRUE.equals(body.get("dismissed"))
        ));
    }
}

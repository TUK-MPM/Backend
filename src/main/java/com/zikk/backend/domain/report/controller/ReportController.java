package com.zikk.backend.domain.report.controller;

import com.zikk.backend.domain.report.dto.PatchReportRequest;
import com.zikk.backend.domain.report.dto.ReportRequest;
import com.zikk.backend.domain.report.dto.ReportResponse;
import com.zikk.backend.domain.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/report")
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<ReportResponse> createReport(@RequestBody ReportRequest request) {
        ReportResponse response = reportService.createReport(request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{reportId}")
    public ResponseEntity<ReportResponse> patchReport(
            @PathVariable Long reportId,
            @RequestBody PatchReportRequest request) {
        ReportResponse response = reportService.patchReport(reportId, request);
        return ResponseEntity.ok(response);
    }
}


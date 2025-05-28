package com.zikk.backend.domain.report.controller;

import com.zikk.backend.domain.report.dto.ReportRequest;
import com.zikk.backend.domain.report.dto.ReportResponse;
import com.zikk.backend.domain.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}


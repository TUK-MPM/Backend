package com.zikk.backend.domain.report.controller;

import com.zikk.backend.domain.admin.entity.Admin;
import com.zikk.backend.domain.report.dto.PatchReportRequest;
import com.zikk.backend.domain.report.dto.ReportDetailResponse;
import com.zikk.backend.domain.report.dto.ReportRequest;
import com.zikk.backend.domain.report.dto.ReportResponse;
import com.zikk.backend.domain.report.service.ReportService;
import com.zikk.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/report")
@Slf4j
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<ReportResponse> createReport(
            @RequestPart("request") ReportRequest request, // JSON
            @RequestPart(value = "images", required = false) List<MultipartFile> images // 이미지들
    ) {
        ReportResponse response = reportService.createReport(request, images);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{reportId}")
    public ResponseEntity<ReportResponse> patchReport(
            @PathVariable Long reportId,
            @RequestPart("request") PatchReportRequest request, // JSON
            @RequestPart(value = "images", required = false) List<MultipartFile> images // 이미지들
    ) {
        ReportResponse response = reportService.patchReport(reportId, request, images);
        return ResponseEntity.ok(response);
    }

    // ✅ 전체 조회 (역할 분기는 서비스 내부에서 처리)
    @GetMapping
    public ResponseEntity<List<ReportResponse>> getReports() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        List<ReportResponse> reports;

        if (principal instanceof Admin admin) {
            log.info("🔍 [getReports] 관리자 접근: {}", admin.getPhone());
            reports = reportService.getAllReports();
        } else if (principal instanceof User user) {
            log.info("🔍 [getReports] 일반 유저 접근: {}", user.getPhone());
            reports = reportService.getReportsByUser(user);
        } else {
            return ResponseEntity.status(403).build(); // 권한 없는 사용자
        }

        log.info("✅ [getReports] 반환된 신고 수: {}", reports.size());
        return ResponseEntity.ok(reports);
    }


    // ✅ 상세 조회: ReportDetailResponse 사용
    @GetMapping("/{reportId}")
    public ResponseEntity<ReportDetailResponse> getReportDetail(
            @PathVariable Long reportId,
            @AuthenticationPrincipal User user) {
        ReportDetailResponse response = reportService.getReportDetail(reportId, user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics")
    public Map<String, Object> getStatistics() {
        return reportService.getStatistics();
    }

    @GetMapping("/examples")
    public List<ReportDetailResponse> getRecentExamples() {
        return reportService.getRecentExamples();
    }
}

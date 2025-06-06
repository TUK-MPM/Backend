package com.zikk.backend.domain.report.dto;

import com.zikk.backend.domain.report.enums.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class ReportResponse {

    private Long reportId;
    private String message; // 생성/수정 응답 시 사용

    // 조회 시 필요한 필드들
    private String reason;           // 신고 사유 (ReportType name 값)
    private ReportStatus status;     // PENDING, APPROVED, REJECTED
    private LocalDateTime createdAt; // 생성 시각
    private LocalDateTime repliedAt; // 처리 시각 (nullable)
    private String address;
}

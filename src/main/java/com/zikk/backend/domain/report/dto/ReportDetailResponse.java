package com.zikk.backend.domain.report.dto;

import com.zikk.backend.domain.report.enums.ReportStatus;
import com.zikk.backend.domain.report.enums.ReportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ReportDetailResponse {

    private String number;                // 신고자 휴대폰 번호
    private String reportId;             // "rep_20250526_001" 형태 식별자
    private ReportType where;            // DOT_BLOCK, PROTECTED_ZONE 등
    private String address;              // 신고 위치 텍스트
    private List<String> mediaUrls;      // S3 업로드 이미지 URL 목록
    private ReportStatus status;         // PENDING, PROCESSING, COMPLETED 등
    private LocalDateTime createdAt;     // 신고 생성 시각
    private LocalDateTime repliedAt;
}
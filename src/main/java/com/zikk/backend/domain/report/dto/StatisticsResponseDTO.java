package com.zikk.backend.domain.report.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class StatisticsResponseDTO {
    private long totalReportCount;
    private long pendingReportCount;
    private long totalInquiryCount;
    private long pendingInquiryCount;
    private Map<String, Long> reportTypes;
}

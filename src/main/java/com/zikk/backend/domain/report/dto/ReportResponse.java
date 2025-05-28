package com.zikk.backend.domain.report.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReportResponse {
    private Long reportId;
    private String message;
}

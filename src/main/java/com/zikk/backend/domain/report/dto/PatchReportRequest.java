package com.zikk.backend.domain.report.dto;

import com.zikk.backend.domain.report.enums.ReportType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PatchReportRequest {
    private String phone;
    private String address;
    private ReportType type;
    private List<String> imageUrls;
}


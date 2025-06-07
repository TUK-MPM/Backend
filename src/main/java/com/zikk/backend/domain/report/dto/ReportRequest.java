package com.zikk.backend.domain.report.dto;

import com.zikk.backend.domain.report.enums.ReportType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReportRequest {
    private String phone;
    private String address;
    private ReportType reportType;
    private List<String> imageUrls;
}

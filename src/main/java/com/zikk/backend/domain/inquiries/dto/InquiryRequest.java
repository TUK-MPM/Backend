package com.zikk.backend.domain.inquiries.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InquiryRequest {
    private String title;
    private String content;
}
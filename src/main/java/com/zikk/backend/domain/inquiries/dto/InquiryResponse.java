package com.zikk.backend.domain.inquiries.dto;

import com.zikk.backend.domain.inquiries.enums.InquiryStatus;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class InquiryResponse {
    private Long inquId;             // 🔹 PK만 사용
    private String title;
    private String content;
    private String response;
    private InquiryStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;
    private String message;
}

package com.zikk.backend.domain.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeDetailResponse {

    private Long notiId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
}

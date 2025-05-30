package com.zikk.backend.domain.notice.vo;

import jakarta.persistence.Embeddable;

import java.time.LocalDateTime;

@Embeddable
public class Content {

    private Long notiId;
    private String title;
    private LocalDateTime createdAt;
}

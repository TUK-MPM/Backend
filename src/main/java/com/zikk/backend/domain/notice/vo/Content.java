package com.zikk.backend.domain.notice.vo;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Embeddable
@Getter
@NoArgsConstructor
public class Content {

    private Long notiId;
    private String title;
    private LocalDateTime createdAt;

    public Content(Long notiId, String title, LocalDateTime createdAt) {
        this.notiId = notiId;
        this.title = title;
        this.createdAt = createdAt;
    }
}

package com.zikk.backend.domain.inquiries.entity;

import com.zikk.backend.domain.admin.entity.Admin;
import com.zikk.backend.domain.inquiries.enums.InquiryStatus;
import com.zikk.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Inquiries {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inquId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String title;
    private String content;
    private String response;

    @Enumerated(EnumType.STRING)
    private InquiryStatus status = InquiryStatus.WAITING;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // TODO: 백엔드에서 날짜 자동처리 할 지, 프론트에서 request 하게 할 지 정해야 함
    private LocalDateTime respondedAt;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;
}


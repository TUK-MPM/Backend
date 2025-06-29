package com.zikk.backend.domain.admin.entity;

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
public class Admin {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminId;

    private String phone;
    private String password;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
}


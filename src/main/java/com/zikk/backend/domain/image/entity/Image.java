package com.zikk.backend.domain.image.entity;

import com.zikk.backend.domain.report.entity.Report;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "report_id")
    private Report report;
}

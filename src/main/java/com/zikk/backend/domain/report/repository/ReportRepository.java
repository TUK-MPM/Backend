package com.zikk.backend.domain.report.repository;

import com.zikk.backend.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}

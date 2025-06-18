package com.zikk.backend.domain.report.repository;

import com.zikk.backend.domain.report.entity.Report;
import com.zikk.backend.domain.report.enums.ReportStatus;
import com.zikk.backend.domain.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findAllByUser(User user);

    long countByStatus(ReportStatus status);

    @Query("SELECT r.reportType, COUNT(r) FROM Report r GROUP BY r.reportType")
    List<Object[]> countReportsByType();

    List<Report> findByStatusOrderByCreatedAtDesc(ReportStatus status, Pageable pageable);

}

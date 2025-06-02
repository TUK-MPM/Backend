package com.zikk.backend.domain.report.repository;

import com.zikk.backend.domain.report.entity.Report;
import com.zikk.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findAllByUser(User user);
}

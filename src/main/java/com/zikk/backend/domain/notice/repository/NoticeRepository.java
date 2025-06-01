package com.zikk.backend.domain.notice.repository;

import com.zikk.backend.domain.notice.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    Page<Notice> findByTitleContaining(String keyword, Pageable pageable);
}

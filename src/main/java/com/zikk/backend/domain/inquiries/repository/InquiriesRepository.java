package com.zikk.backend.domain.inquiries.repository;

import com.zikk.backend.domain.inquiries.entity.Inquiries;
import com.zikk.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiriesRepository extends JpaRepository<Inquiries, Long> {
    List<Inquiries> findAllByUser(User user);
}
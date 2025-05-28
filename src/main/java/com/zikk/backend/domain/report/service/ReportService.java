package com.zikk.backend.domain.report.service;

import com.zikk.backend.domain.image.entity.Image;
import com.zikk.backend.domain.report.dto.ReportRequest;
import com.zikk.backend.domain.report.dto.ReportResponse;
import com.zikk.backend.domain.report.entity.Report;
import com.zikk.backend.domain.report.enums.ReportStatus;
import com.zikk.backend.domain.report.repository.ReportRepository;
import com.zikk.backend.domain.user.entity.User;
import com.zikk.backend.domain.user.repository.UserRepository;
import com.zikk.backend.global.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final S3Uploader s3Uploader;

    public ReportResponse createReport(ReportRequest request) {
        // 1. phone으로 유저 조회, 없으면 자동 회원가입
        User user = userRepository.findByPhone(request.getPhone())
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setPhone(request.getPhone());
                    return userRepository.save(newUser);
                });

        // 2. 신고 객체 생성
        Report report = new Report();
        report.setUser(user);
        report.setLocation(request.getAddress());
        report.setReason(request.getType().name());
        report.setStatus(ReportStatus.PENDING);  // 기본 상태

        // 3. 이미지 URL → S3 업로드 → Image 엔티티 생성 → 연관
        for (String imageUrl : request.getImageUrls()) {
            try (InputStream in = new URL(imageUrl).openStream()) {
                String uploadedUrl = s3Uploader.upload(in, UUID.randomUUID() + ".jpg", "report-images");

                Image image = new Image();
                image.setImageUrl(uploadedUrl);
                image.setReport(report); // 연관 설정

                report.getImageUrls().add(image); // 양방향 연관 등록
            } catch (Exception e) {
                throw new RuntimeException("이미지 업로드 실패: " + imageUrl, e);
            }
        }

        Report savedReport = reportRepository.save(report);
        return new ReportResponse(savedReport.getReportId(), "신고가 정상적으로 접수되었습니다.");
    }
}
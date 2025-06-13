package com.zikk.backend.domain.report.service;

import com.zikk.backend.domain.admin.entity.Admin;
import com.zikk.backend.domain.image.entity.Image;
import com.zikk.backend.domain.inquiries.enums.InquiryStatus;
import com.zikk.backend.domain.inquiries.repository.InquiriesRepository;
import com.zikk.backend.domain.report.dto.PatchReportRequest;
import com.zikk.backend.domain.report.dto.ReportDetailResponse;
import com.zikk.backend.domain.report.dto.ReportRequest;
import com.zikk.backend.domain.report.dto.ReportResponse;
import com.zikk.backend.domain.report.entity.Report;
import com.zikk.backend.domain.report.enums.ReportStatus;
import com.zikk.backend.domain.report.enums.ReportType;
import com.zikk.backend.domain.report.repository.ReportRepository;
import com.zikk.backend.domain.user.entity.User;
import com.zikk.backend.domain.user.repository.UserRepository;
import com.zikk.backend.global.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final S3Uploader s3Uploader;
    private final InquiriesRepository inquiriesRepository;

    @Transactional
    public ReportResponse createReport(ReportRequest request, List<MultipartFile> images) {
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
        report.setReportType(request.getReportType());
        report.setStatus(ReportStatus.PENDING);  // 기본 상태

        // 3. 이미지 처리
        if (images != null && !images.isEmpty()) {
            for (MultipartFile imageFile : images) {
                try (InputStream in = imageFile.getInputStream()) {
                    String uploadedUrl = s3Uploader.upload(in, UUID.randomUUID() + ".jpg", "report-images");

                    Image image = new Image();
                    image.setImageUrl(uploadedUrl);
                    image.setReport(report); // 연관 설정

                    report.getImageUrls().add(image); // 양방향 연관 등록
                } catch (Exception e) {
                    throw new RuntimeException("이미지 업로드 실패: " + imageFile.getOriginalFilename(), e);
                }
            }
        }

        Report savedReport = reportRepository.save(report);
        return ReportResponse.builder()
                .reportId(savedReport.getReportId())
                .message("신고가 정상적으로 접수되었습니다.")
                .reason(savedReport.getReportType().name())
                .status(savedReport.getStatus())
                .address(savedReport.getLocation())
                .createdAt(savedReport.getCreatedAt())
                .build();
    }

    @Transactional
    public ReportResponse patchReport(Long reportId, PatchReportRequest request, List<MultipartFile> images) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new NoSuchElementException("해당 신고가 존재하지 않습니다."));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        // ✅ 관리자 → status만 수정
        if (principal instanceof Admin) {
            boolean updated = false;

            if (request.getStatus() != null) {
                report.setStatus(request.getStatus());
                report.setRepliedAt(LocalDateTime.now());
                updated = true;
            }

            if (!updated) {
                throw new IllegalArgumentException("수정할 값이 필요합니다.");
            }

            return ReportResponse.builder()
                    .reportId(report.getReportId())
                    .message("신고가 정상적으로 수정되었습니다.")
                    .build();
        }

        // ✅ 일반 유저 → phone, location, reportType, images만 수정
        if (principal instanceof User user) {
            if (!report.getUser().getUserId().equals(user.getUserId())) {
                throw new SecurityException("본인의 신고만 수정할 수 있습니다.");
            }

            if (request.getPhone() != null) {
                User updateUser = userRepository.findByPhone(request.getPhone())
                        .orElseGet(() -> {
                            User newUser = new User();
                            newUser.setPhone(request.getPhone());
                            return userRepository.save(newUser);
                        });
                report.setUser(updateUser);
            }

            if (request.getAddress() != null) {
                report.setLocation(request.getAddress());
            }

            if (request.getReportType() != null) {
                report.setReportType(request.getReportType());
            }

            if (images != null) {
                report.getImageUrls().clear();

                for (MultipartFile imageFile : images) {
                    try (InputStream in = imageFile.getInputStream()) {
                        String uploadedUrl = s3Uploader.upload(in, UUID.randomUUID() + ".jpg", "report-images");

                        Image image = new Image();
                        image.setImageUrl(uploadedUrl);
                        image.setReport(report);

                        report.getImageUrls().add(image);
                    } catch (Exception e) {
                        throw new RuntimeException("이미지 업로드 실패: " + imageFile.getOriginalFilename(), e);
                    }
                }
            }

            return ReportResponse.builder()
                    .reportId(report.getReportId())
                    .message("신고가 정상적으로 수정되었습니다.")
                    .build();
        }

        throw new SecurityException("유효하지 않은 사용자입니다.");
    }

    @Transactional(readOnly = true)
    public List<ReportResponse> getReportsByUser(User user) {
        List<Report> reports = reportRepository.findAllByUser(user);

        return reports.stream()
                .map(report -> ReportResponse.builder()
                        .reportId(report.getReportId())
                        .reason(report.getReportType().name())
                        .status(report.getStatus())
                        .address(report.getLocation())
                        .createdAt(report.getCreatedAt())
                        .repliedAt(report.getRepliedAt())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public ReportResponse getReportById(Long reportId, User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals("ROLE_ADMIN"));

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new NoSuchElementException("해당 신고가 존재하지 않습니다."));

        // 일반 유저는 자신의 신고만 조회 가능
        if (!isAdmin && !report.getUser().getUserId().equals(user.getUserId())) {
            throw new SecurityException("해당 신고에 접근할 수 없습니다.");
        }

        return ReportResponse.builder()
                .reportId(report.getReportId())
                .reason(report.getReportType().name())
                .status(report.getStatus())
                .createdAt(report.getCreatedAt())
                .repliedAt(report.getRepliedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public ReportDetailResponse getReportDetail(Long reportId, User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals("ROLE_ADMIN"));

        Report report;

        // 관리자면 전체 조회 허용, 일반 유저는 본인 신고만 허용
        if (isAdmin) {
            report = reportRepository.findById(reportId)
                    .orElseThrow(() -> new NoSuchElementException("신고가 존재하지 않습니다."));
        } else {
            report = reportRepository.findById(reportId)
                    .filter(r -> r.getUser().getUserId().equals(user.getUserId()))
                    .orElseThrow(() -> new SecurityException("본인의 신고만 조회할 수 있습니다."));
        }

        return ReportDetailResponse.builder()
                .number(report.getUser().getPhone())
                .reportId("rep_" + report.getCreatedAt().toLocalDate().toString().replaceAll("-", "") + "_" + String.format("%03d", report.getReportId()))
                .reportType(report.getReportType())
                .address(report.getLocation())
                .mediaUrls(report.getImageUrls().stream()
                        .map(image -> image.getImageUrl())
                        .toList())
                .status(report.getStatus())
                .createdAt(report.getCreatedAt())
                .repliedAt(report.getRepliedAt())
                .build();
    }
    @Transactional(readOnly = true)
    public List<ReportResponse> getAllReports() {
        List<Report> reports = reportRepository.findAll();

        return reports.stream()
                .map(report -> ReportResponse.builder()
                        .reportId(report.getReportId())
                        .reason(report.getReportType().name())
                        .status(report.getStatus())
                        .address(report.getLocation())
                        .createdAt(report.getCreatedAt())
                        .repliedAt(report.getRepliedAt())
                        .build())
                .toList();
    }

    public Map<String, Object> getStatistics() {
        long totalReportCount = reportRepository.count();
        long pendingReportCount = reportRepository.countByStatus(ReportStatus.PENDING);

        long totalInquiryCount = inquiriesRepository.count();
        long pendingInquiryCount = inquiriesRepository.countByStatus(InquiryStatus.WAITING);

        List<Object[]> reportTypeCounts = reportRepository.countReportsByType();
        Map<String, Long> reportTypes = new HashMap<>();

        for (Object[] row : reportTypeCounts) {
            String type = row[0].toString(); // Enum → String
            Long count = (Long) row[1];
            reportTypes.put(type, count);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalReportCount", totalReportCount);
        result.put("pendingReportCount", pendingReportCount);
        result.put("totalInquiryCount", totalInquiryCount);
        result.put("pendingInquiryCount", pendingInquiryCount);
        result.put("reportTypes", reportTypes);

        return result;
    }
}

package com.zikk.backend.domain.notice.service;

import com.zikk.backend.domain.admin.entity.Admin;
import com.zikk.backend.domain.admin.repository.AdminRepository;
import com.zikk.backend.domain.notice.dto.NoticeDetailResponse;
import com.zikk.backend.domain.notice.dto.NoticeListResponse;
import com.zikk.backend.domain.notice.dto.NoticeRequest;
import com.zikk.backend.domain.notice.dto.NoticeResponse;
import com.zikk.backend.domain.notice.entity.Notice;
import com.zikk.backend.domain.notice.enums.SortType;
import com.zikk.backend.domain.notice.repository.NoticeRepository;
import com.zikk.backend.domain.notice.vo.Content;
import com.zikk.backend.global.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final AdminRepository adminRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public NoticeResponse createNotice(NoticeRequest request, String authHeader) {
        // 1. 토큰 파싱
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization 헤더가 유효하지 않습니다.");
        }

        String token = authHeader.replace("Bearer ", "");

        // 2. 권한 확인
        String role = jwtTokenProvider.getRole(token);
        if (!"ROLE_ADMIN".equals(role)) {
            throw new SecurityException("관리자 권한이 필요합니다.");
        }

        // 3. adminId 추출 및 검증
        Long adminId = jwtTokenProvider.getUserId(token);
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));

        // 4. 공지 생성
        Notice notice = new Notice();
        notice.setAdmin(admin);
        notice.setTitle(request.getTitle());
        notice.setContent(request.getContent());
        notice.setViews(0L);

        Notice saved = noticeRepository.save(notice);
        return new NoticeResponse(saved.getNotiId(), "공지사항이 정상적으로 등록되었습니다.");
    }

    public NoticeListResponse getNoticeList(int page, int size, String keyword, SortType sortType) {
        Sort sort = switch (sortType) {
            case VIEWS -> Sort.by(Sort.Direction.DESC, "views");
            case LATEST -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Notice> noticePage;
        if (keyword != null && !keyword.isBlank()) {
            noticePage = noticeRepository.findByTitleContaining(keyword, pageable);
        } else {
            noticePage = noticeRepository.findAll(pageable);
        }

        List<Content> contentList = noticePage.getContent().stream()
                .map(n -> new Content(n.getNotiId(), n.getTitle(), n.getCreatedAt()))
                .toList();

        return new NoticeListResponse(
                contentList,
                noticePage.getTotalPages(),
                noticePage.hasNext(),
                noticePage.hasPrevious(),
                noticePage.isFirst(),
                noticePage.isLast()
        );
    }

    @Transactional
    public NoticeDetailResponse getNoticeDetail(Long notiId) {
        Notice notice = noticeRepository.findById(notiId)
                .orElseThrow(() -> new NoSuchElementException("공지사항을 찾을 수 없습니다."));

        // 조회수 증가
        notice.setViews(notice.getViews() + 1);

        return new NoticeDetailResponse(
                notice.getNotiId(),
                notice.getTitle(),
                notice.getContent()
        );
    }

    @Transactional
    public NoticeResponse patchNotice(Long notiId, NoticeRequest request, String authHeader) {
        // 1. 관리자 인증
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization 헤더가 유효하지 않습니다.");
        }

        String token = authHeader.replace("Bearer ", "");
        String role = jwtTokenProvider.getRole(token);
        if (!"ROLE_ADMIN".equals(role)) {
            throw new SecurityException("관리자 권한이 필요합니다.");
        }

        // 2. 공지사항 조회 및 수정
        Notice notice = noticeRepository.findById(notiId)
                .orElseThrow(() -> new NoSuchElementException("공지사항을 찾을 수 없습니다."));

        if (request.getTitle() != null) {
            notice.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            notice.setContent(request.getContent());
        }

        return new NoticeResponse(notice.getNotiId(), "공지사항이 수정되었습니다.");
    }

    @Transactional
    public NoticeResponse deleteNotice(Long notiId, String authHeader) {
        // 1. 관리자 인증
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization 헤더가 유효하지 않습니다.");
        }

        String token = authHeader.replace("Bearer ", "");
        String role = jwtTokenProvider.getRole(token);
        if (!"ROLE_ADMIN".equals(role)) {
            throw new SecurityException("관리자 권한이 필요합니다.");
        }

        // 2. 공지사항 조회 및 삭제
        Notice notice = noticeRepository.findById(notiId)
                .orElseThrow(() -> new NoSuchElementException("공지사항을 찾을 수 없습니다."));

        noticeRepository.delete(notice);

        return new NoticeResponse(notiId, "공지사항이 삭제되었습니다.");
    }
}


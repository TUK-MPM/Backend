package com.zikk.backend.domain.inquiries.service;

import com.zikk.backend.domain.admin.entity.Admin;
import com.zikk.backend.domain.inquiries.dto.InquiryRequest;
import com.zikk.backend.domain.inquiries.dto.InquiryResponse;
import com.zikk.backend.domain.inquiries.entity.Inquiries;
import com.zikk.backend.domain.inquiries.enums.InquiryStatus;
import com.zikk.backend.domain.inquiries.repository.InquiriesRepository;
import com.zikk.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiriesRepository inquiriesRepository;

    /*
      1. 사용자 문의 작성
     */
    public InquiryResponse createInquiry(User user, InquiryRequest dto) {
        Inquiries inquiry = new Inquiries();
        inquiry.setUser(user);
        inquiry.setTitle(dto.getTitle());
        inquiry.setContent(dto.getContent());

        Inquiries saved = inquiriesRepository.save(inquiry);

        return toResponse(saved, "문의가 정상적으로 접수되었습니다.");
    }

    /*
      2. 사용자 본인 문의 목록 조회
     */
    public List<InquiryResponse> getMyInquiries(User user) {
        return inquiriesRepository.findAllByUser(user).stream()
                .map(inquiry -> toResponse(inquiry, "<User> 정상 조회 되었습니다."))
                .collect(Collectors.toList());
    }

    /*
      3. 관리자 전체 문의 목록 조회
     */
    public List<InquiryResponse> getAllInquiries() {
        return inquiriesRepository.findAll().stream()
                .map(inquiry -> toResponse(inquiry, "<Admin> 정상 조회 되었습니다."))
                .collect(Collectors.toList());
    }

    /**
     * 🔹 4. 관리자: 특정 문의 상세 조회
     */
    public InquiryResponse getInquiryById(Long inquId) {
        Inquiries inquiry = inquiriesRepository.findById(inquId)
                .orElseThrow(() -> new NoSuchElementException("해당 문의가 존재하지 않습니다."));
        return toResponse(inquiry, "<Admin> 상세 정상 조회 되었습니다.");
    }

    /*
      5. 사용자: 본인 문의 상세 조회
     */
    public InquiryResponse getInquiryByIdAndUser(Long inquId, User user) {
        Inquiries inquiry = inquiriesRepository.findById(inquId)
                .filter(i -> i.getUser().getUserId().equals(user.getUserId()))
                .orElseThrow(() -> new NoSuchElementException("해당 문의가 존재하지 않거나 권한이 없습니다."));
        return toResponse(inquiry, "<User> 상세 정상 조회 되었습니다.");
    }

    /*
      6. 관리자: 답변 등록
     */
    public InquiryResponse answerInquiry(Long inquId, Admin admin, InquiryResponse inquiryResponse) {
        Inquiries inquiry = inquiriesRepository.findById(inquId)
                .orElseThrow(() -> new NoSuchElementException("해당 문의가 존재하지 않습니다."));

        inquiry.setResponse(inquiryResponse.getResponse());
        inquiry.setStatus(InquiryStatus.COMPLETED);
        inquiry.setRespondedAt(LocalDateTime.now());
        inquiry.setAdmin(admin);

        Inquiries updated = inquiriesRepository.save(inquiry);
        return toResponse(updated, "답변이 등록되었습니다.");
    }

    /*
      공통 응답 생성
     */
    private InquiryResponse toResponse(Inquiries inquiry, String message) {
        return InquiryResponse.builder()
                .inquId(inquiry.getInquId())           // 실제 ID
                .title(inquiry.getTitle())             // 제목
                .content(inquiry.getContent())         // 내용
                .response(inquiry.getResponse())       // 관리자 응답
                .status(inquiry.getStatus())           // 상태
                .createdAt(inquiry.getCreatedAt())     // 생성일
                .respondedAt(inquiry.getRespondedAt()) // 응답일
                .message(message)                      // 부가 메시지
                .build();
    }
}

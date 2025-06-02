package com.zikk.backend.domain.inquiries.controller;

import com.zikk.backend.domain.admin.entity.Admin;
import com.zikk.backend.domain.inquiries.dto.InquiryRequest;
import com.zikk.backend.domain.inquiries.dto.InquiryResponse;
import com.zikk.backend.domain.inquiries.service.InquiryService;
import com.zikk.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inquiries")
public class InquiryController {

    private final InquiryService inquiryService;

    /*
      [POST] 문의 작성 (일반 사용자)
     */
    @PostMapping
    public ResponseEntity<InquiryResponse> createInquiry(@AuthenticationPrincipal User user,
                                                         @RequestBody InquiryRequest dto) {
        InquiryResponse response = inquiryService.createInquiry(user, dto);
        return ResponseEntity.ok(response);
    }

    /*
      [GET] 문의 목록 조회
      - 사용자: 본인의 문의만
      - 관리자: 전체 문의 조회
     */
    @GetMapping
    public ResponseEntity<List<InquiryResponse>> getInquiries() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof User user) {
            List<InquiryResponse> list = inquiryService.getMyInquiries(user);
            return ResponseEntity.ok(list);

        } else if (principal instanceof Admin admin) {
            List<InquiryResponse> list = inquiryService.getAllInquiries();
            return ResponseEntity.ok(list);
        }

        return ResponseEntity.status(401).build(); // 인증되지 않은 사용자
    }

    /*
      [GET] 문의 상세 조회
      - 사용자: 본인 문의만
      - 관리자: 전체 문의 중 선택
     */
    @GetMapping("/{inquId}")
    public ResponseEntity<InquiryResponse> getInquiryDetail(@PathVariable Long inquId) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof Admin) {
            InquiryResponse result = inquiryService.getInquiryById(inquId);
            return ResponseEntity.ok(result);

        } else if (principal instanceof User user) {
            InquiryResponse result = inquiryService.getInquiryByIdAndUser(inquId, user);
            return ResponseEntity.ok(result);
        }

        return ResponseEntity.status(401).build(); // 인증되지 않은 사용자
    }


     // [PATCH] 관리자 답변 등록
    @PatchMapping("/{inquId}")
    public ResponseEntity<InquiryResponse> answerInquiry(@PathVariable Long inquId,
                                                         @RequestBody InquiryResponse inquiryResponse) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof Admin admin) {
            InquiryResponse result = inquiryService.answerInquiry(inquId, admin, inquiryResponse);
            return ResponseEntity.ok(result);
        }

        return ResponseEntity.status(403).build(); // 관리자만 접근 가능
    }
}

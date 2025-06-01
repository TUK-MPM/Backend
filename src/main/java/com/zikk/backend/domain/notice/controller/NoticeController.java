package com.zikk.backend.domain.notice.controller;

import com.zikk.backend.domain.notice.dto.NoticeDetailResponse;
import com.zikk.backend.domain.notice.dto.NoticeListResponse;
import com.zikk.backend.domain.notice.dto.NoticeRequest;
import com.zikk.backend.domain.notice.dto.NoticeResponse;
import com.zikk.backend.domain.notice.enums.SortType;
import com.zikk.backend.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notice")
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping
    public ResponseEntity<NoticeResponse> createNotice(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody NoticeRequest request) {
        return ResponseEntity.ok(noticeService.createNotice(request, authHeader));
    }

    @GetMapping
    public ResponseEntity<NoticeListResponse> getNoticeList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "LATEST") SortType sortType
    ) {
        return ResponseEntity.ok(noticeService.getNoticeList(page, size, keyword, sortType));
    }

    @GetMapping("/{notiId}")
    public ResponseEntity<NoticeDetailResponse> getNoticeDetail(@PathVariable Long notiId) {
        return ResponseEntity.ok(noticeService.getNoticeDetail(notiId));
    }


    @PatchMapping("/{notiId}")
    public ResponseEntity<NoticeResponse> patchNotice(@PathVariable Long notiId,
                                                      @RequestHeader("Authorization") String authHeader,
                                                      @RequestBody NoticeRequest request) {
        return ResponseEntity.ok(noticeService.patchNotice(notiId, request, authHeader));
    }

    @DeleteMapping("/{notiId}")
    public ResponseEntity<NoticeResponse> deleteNotice(@PathVariable Long notiId,
                                                       @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(noticeService.deleteNotice(notiId, authHeader));
    }

}


package com.zikk.backend.domain.auth.controller;

import com.zikk.backend.domain.auth.dto.AdminLoginRequest;
import com.zikk.backend.domain.auth.dto.UserLoginRequest;
import com.zikk.backend.domain.auth.dto.LoginResponse;
import com.zikk.backend.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/user/login")
    public ResponseEntity<LoginResponse> userLogin(@RequestBody UserLoginRequest request) {
        return ResponseEntity.ok(authService.userLogin(request));
    }

    @PostMapping("/admin/login")
    public ResponseEntity<LoginResponse> adminLogin(@RequestBody AdminLoginRequest request) {
        return ResponseEntity.ok(authService.adminLogin(request));
    }
}

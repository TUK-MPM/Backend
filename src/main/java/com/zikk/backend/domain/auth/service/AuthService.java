package com.zikk.backend.domain.auth.service;

import com.zikk.backend.domain.admin.entity.Admin;
import com.zikk.backend.domain.admin.repository.AdminRepository;
import com.zikk.backend.domain.auth.dto.LoginRequest;
import com.zikk.backend.domain.auth.dto.LoginResponse;
import com.zikk.backend.domain.user.entity.User;
import com.zikk.backend.domain.user.repository.UserRepository;
import com.zikk.backend.global.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginResponse login(LoginRequest request) {
        String phone = request.getPhone();

        // ✅ 관리자 존재 여부 확인
        Optional<Admin> adminOpt = adminRepository.findByPhone(phone);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            String token = jwtTokenProvider.generateToken(admin.getAdminId(), "ROLE_ADMIN");
            return new LoginResponse(admin.getAdminId(), token);
        }

        // ✅ 유저 확인 or 새로 생성
        User user = userRepository.findByPhone(phone).orElseGet(() -> {
            User newUser = new User();
            newUser.setPhone(phone);
            return userRepository.save(newUser);
        });

        String token = jwtTokenProvider.generateToken(user.getUserId(), "ROLE_USER");
        return new LoginResponse(user.getUserId(), token);
    }
}


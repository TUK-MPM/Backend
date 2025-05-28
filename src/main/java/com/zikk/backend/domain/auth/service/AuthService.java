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

        Optional<Admin> adminOpt = adminRepository.findByPhone(phone);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            String token = jwtTokenProvider.generateToken(admin.getAdminId(), "ROLE_ADMIN");
            return new LoginResponse(admin.getAdminId(), token);
        }

        Optional<User> userOpt = userRepository.findByPhone(phone);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String token = jwtTokenProvider.generateToken(user.getUserId(), "ROLE_USER");
            return new LoginResponse(user.getUserId(), token);
        }

        throw new UsernameNotFoundException("존재하지 않는 사용자입니다.");
    }
}


package com.zikk.backend.domain.auth.service;

import com.zikk.backend.domain.admin.entity.Admin;
import com.zikk.backend.domain.admin.repository.AdminRepository;
import com.zikk.backend.domain.auth.dto.AdminLoginRequest;
import com.zikk.backend.domain.auth.dto.UserLoginRequest;
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

    public LoginResponse userLogin(UserLoginRequest request) {
        String phone = request.getPhone();

        User user = userRepository.findByPhone(phone).orElseGet(() -> {
            User newUser = new User();
            newUser.setPhone(phone);
            return userRepository.save(newUser);
        });

        String token = jwtTokenProvider.generateToken(user.getUserId(), "ROLE_USER");
        return new LoginResponse(user.getUserId(), token);
    }

    public LoginResponse adminLogin(AdminLoginRequest request) {
        String phone = request.getPhone();
        String password = request.getPassword();

        Admin admin = adminRepository.findByPhone(phone)
                .orElseThrow(() -> new UsernameNotFoundException("관리자 정보를 찾을 수 없습니다."));

        if (!admin.getPassword().equals(password)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtTokenProvider.generateToken(admin.getAdminId(), "ROLE_ADMIN");
        return new LoginResponse(admin.getAdminId(), token);
    }
}



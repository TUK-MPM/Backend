package com.zikk.backend.global;

import com.zikk.backend.domain.admin.repository.AdminRepository;
import com.zikk.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults()) // 🔹 CORS 활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/user/login").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasRole("USER")
                        .anyRequest().permitAll()
                )
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .addFilterBefore(
                new JwtAuthenticationFilter(jwtTokenProvider, userRepository, adminRepository),
                UsernamePasswordAuthenticationFilter.class
        );


        return http.build();
    }

    // CORS 설정 등록
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*"); // 모든 Origin 허용 (필요시 제한)
        config.addAllowedMethod("*");        // 모든 HTTP 메서드 허용 (GET, POST, PUT, DELETE 등)
        config.addAllowedHeader("*");        // 모든 Header 허용
        config.setAllowCredentials(true);    // 인증 정보 포함 허용 (Authorization 등)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}




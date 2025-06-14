package com.zikk.backend.domain.auth.dto;
import lombok.Getter;

@Getter
public class AdminLoginRequest {

    private String phone;
    private String password;
}

package com.abs.app.application.auth.dto;

import com.abs.app.common.constant.AuthConstant;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDto {
    @NotBlank(message = AuthConstant.EMAIL_REQUIRED)
    private String email;

    @NotBlank(message = AuthConstant.PASSWORD_REQUIRED)
    private String password;

    private boolean rememberMe;
}

package com.abs.app.application.auth.dto;

import com.abs.app.common.constant.AuthConstant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyOtpRequestDto {
    @NotBlank(message = AuthConstant.EMAIL_REQUIRED)
    @Email(message = AuthConstant.EMAIL_INVALID)
    private String email;

    @NotBlank(message = AuthConstant.OTP_REQUIRED)
    private String otp;
}

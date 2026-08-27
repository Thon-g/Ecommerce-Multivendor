package com.abs.app.application.auth.dto;

import com.abs.app.common.constant.AuthConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequestDto {
    @NotBlank(message = AuthConstant.INVALID_TOKEN)
    private String token;

    @NotBlank(message = AuthConstant.NEW_PASSWORD_REQUIRED)
    @Size(min = 6, max = 10)
    private String newPassword;
}

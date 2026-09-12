package com.abs.app.application.auth.dto;

import com.abs.app.common.constant.AuthConstant;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordRequestDto {
    @NotBlank(message = AuthConstant.CURRENT_PASSWORD_REQUIRED)
    private String currentPassword;

    @NotBlank(message = AuthConstant.NEW_PASSWORD_REQUIRED)
    private String newPassword;

    @NotBlank(message = AuthConstant.CONFIRM_PASSWORD_REQUIRED)
    private String rePassword;
}

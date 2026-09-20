package com.abs.app.application.auth.dto;

import com.abs.app.common.constant.AuthConstant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDto {
    @NotBlank(message = AuthConstant.EMAIL_REQUIRED)
    @Size(min = 5, max = 100)
    @Email(message = AuthConstant.EMAIL_INVALID)
    private String email;

    @NotBlank(message = AuthConstant.PASSWORD_REQUIRED)
    @Size(min = 6, max = 10)
    private String password;

    @NotBlank(message = AuthConstant.FIRST_NAME_REQUIRED)
    @Size(min = 3, max = 6)
    private String firstName;

    @NotBlank(message = AuthConstant.LAST_NAME_REQUIRED)
    @Size(min = 3, max = 6)
    private String lastName;
}

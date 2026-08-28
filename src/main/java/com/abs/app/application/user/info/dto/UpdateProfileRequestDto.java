package com.abs.app.application.user.info.dto;

import com.abs.app.common.constant.AuthConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequestDto {
    @NotBlank(message = AuthConstant.FIRST_NAME_REQUIRED)
    @Size(min = 3, max = 6)
    private String firstName;

    @NotBlank(message = AuthConstant.LAST_NAME_REQUIRED)
    @Size(min = 3, max = 6)
    private String lastName;

    @NotBlank(message = AuthConstant.PHONE_NUMBER_REQUIRED)
    @Pattern(regexp = "^(03|05|07|08|09)\\d{8}$", message = AuthConstant.INVALID_PHONE_NUMBER)
    private String phoneNumber;
    private boolean gender;
}

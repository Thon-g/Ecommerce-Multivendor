package com.abs.app.application.seller.profile.dto;

import com.abs.app.common.constant.SellerConstant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSellerProfileRequestDto {

    @NotBlank(message = SellerConstant.BUSINESS_NAME_REQUIRED)
    @Size(max = 100, message = SellerConstant.BUSINESS_NAME_MAX_LENGTH)
    private String businessName;

    @NotBlank(message = SellerConstant.BUSINESS_EMAIL_REQUIRED)
    @Email(message = SellerConstant.BUSINESS_EMAIL_INVALID)
    private String businessEmail;

    @NotBlank(message = SellerConstant.BUSINESS_PHONE_REQUIRED)
    @Pattern(regexp = "^[0-9]{9,15}$", message = SellerConstant.BUSINESS_PHONE_INVALID)
    private String businessPhone;

    @NotBlank(message = SellerConstant.BUSINESS_ADDRESS_REQUIRED)
    private String businessAddress;
}

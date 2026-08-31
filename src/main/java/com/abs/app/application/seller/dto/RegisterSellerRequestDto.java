package com.abs.app.application.seller.dto;

import com.abs.app.common.constant.SellerConstant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterSellerRequestDto {

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

    @NotBlank(message = SellerConstant.ACCOUNT_NAME_REQUIRED)
    private String accountName;

    @NotBlank(message = SellerConstant.ACCOUNT_HOLDER_NAME_REQUIRED)
    private String accountHolderName;

    @NotBlank(message = SellerConstant.IFSC_CODE_REQUIRED)
    private String ifscCode;

    @NotBlank(message = SellerConstant.PICKUP_NAME_REQUIRED)
    private String pickupName;

    @NotBlank(message = SellerConstant.PICKUP_LOCALITY_REQUIRED)
    private String pickupLocality;

    @NotBlank(message = SellerConstant.PICKUP_ADDRESS_REQUIRED)
    private String pickupAddress;

    @NotBlank(message = SellerConstant.PICKUP_CITY_REQUIRED)
    private String pickupCity;

    @NotBlank(message = SellerConstant.PICKUP_STATE_REQUIRED)
    private String pickupState;

    @NotBlank(message = SellerConstant.PICKUP_PINCODE_REQUIRED)
    private String pickupPinCode;

    @NotBlank(message = SellerConstant.PICKUP_PHONE_REQUIRED)
    @Pattern(regexp = "^[0-9]{9,15}$", message = SellerConstant.PICKUP_PHONE_INVALID)
    private String pickupPhone;

    private String gstin;
}

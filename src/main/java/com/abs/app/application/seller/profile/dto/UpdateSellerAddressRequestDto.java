package com.abs.app.application.seller.profile.dto;

import com.abs.app.common.constant.SellerConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSellerAddressRequestDto {

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
}

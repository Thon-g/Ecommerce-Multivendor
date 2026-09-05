package com.abs.app.application.user.cart.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplyCouponRequestDto {
    @NotBlank(message = "Coupon code is required")
    private String couponCode;
}

package com.abs.app.application.user.cart.dto;

import com.abs.app.common.constant.CartConstant;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplyCouponRequestDto {
    @NotBlank(message = CartConstant.COUPON_CODE_REQUIRED)
    private String couponCode;
}

package com.abs.app.application.user.cart.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApplyCouponToCartCommand {
    private String userId;
    private String couponCode;
}

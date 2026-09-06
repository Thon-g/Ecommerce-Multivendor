package com.abs.app.application.user.cart.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RemoveCouponFromCartCommand {
    private String userId;
}

package com.abs.app.application.user.cart.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateCartItemQuantityCommand {
    private String userId;
    private Long cartItemId;
    private Integer quantity;
}

package com.abs.app.application.user.cart.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RemoveCartItemCommand {
    private String userId;
    private Long cartItemId;
}

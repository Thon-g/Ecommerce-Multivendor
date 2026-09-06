package com.abs.app.application.user.cart.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddToCartCommand {
    private String userId;
    private String productId;
    private String size;
    private Integer quantity;
}

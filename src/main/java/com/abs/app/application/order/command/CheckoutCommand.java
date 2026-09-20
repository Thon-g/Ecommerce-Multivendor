package com.abs.app.application.order.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CheckoutCommand {
    private String userId;
    private Long addressId;
}

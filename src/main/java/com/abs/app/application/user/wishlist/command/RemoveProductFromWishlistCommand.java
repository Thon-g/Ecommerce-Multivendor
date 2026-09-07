package com.abs.app.application.user.wishlist.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RemoveProductFromWishlistCommand {
    private String userId;
    private String productId;
}

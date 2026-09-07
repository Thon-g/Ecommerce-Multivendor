package com.abs.app.infrastructure.mapper;

import com.abs.app.application.user.wishlist.dto.WishlistResponseDto;
import com.abs.app.domain.entity.Wishlist;
import java.util.stream.Collectors;

public class WishlistMapper {
    public static WishlistResponseDto toWishlistResponseDto(Wishlist wishlist) {
        WishlistResponseDto dto = new WishlistResponseDto();
        dto.setId(wishlist.getId());
        dto.setUserId(wishlist.getUser().getUserId());
        if (wishlist.getProducts() != null) {
            dto.setProducts(wishlist.getProducts().stream()
                    .map(ProductMapper::toProductResponseDto)
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}

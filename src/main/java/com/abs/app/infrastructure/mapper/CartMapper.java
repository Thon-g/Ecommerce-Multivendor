package com.abs.app.infrastructure.mapper;

import com.abs.app.application.user.cart.dto.CartItemResponseDto;
import com.abs.app.application.user.cart.dto.CartResponseDto;
import com.abs.app.domain.entity.Cart;
import com.abs.app.domain.entity.CartItem;

import java.util.stream.Collectors;

public class CartMapper {
    public static CartResponseDto toCartResponseDto(Cart cart) {
        if (cart == null) return null;
        CartResponseDto dto = new CartResponseDto();
        dto.setId(cart.getId());
        dto.setTotalSellingPrice(cart.getTotalSellingPrice());
        dto.setTotalItem(cart.getTotalItem());
        dto.setTotalMrpPrice(cart.getTotalMrpPrice());
        dto.setDiscount(cart.getDiscount());
        dto.setCouponCode(cart.getCouponCode());
        if (cart.getCartItems() != null) {
            dto.setCartItems(cart.getCartItems().stream()
                    .map(CartMapper::toCartItemResponseDto)
                    .collect(Collectors.toSet()));
        }
        return dto;
    }

    public static CartItemResponseDto toCartItemResponseDto(CartItem cartItem) {
        if (cartItem == null) return null;
        CartItemResponseDto dto = new CartItemResponseDto();
        dto.setId(cartItem.getId());
        dto.setProduct(ProductMapper.toProductResponseDto(cartItem.getProduct()));
        dto.setSize(cartItem.getSize());
        dto.setQuantity(cartItem.getQuantity());
        dto.setMrpPrice(cartItem.getMrpPrice());
        dto.setSellingPrice(cartItem.getSellingPrice());
        dto.setUserId(cartItem.getUserId());
        return dto;
    }
}

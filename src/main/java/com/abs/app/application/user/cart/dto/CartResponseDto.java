package com.abs.app.application.user.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartResponseDto {
    private Long id;
    private Set<CartItemResponseDto> cartItems;
    private Double totalSellingPrice;
    private Integer totalItem;
    private Integer totalMrpPrice;
    private Integer discount;
    private String couponCode;
}

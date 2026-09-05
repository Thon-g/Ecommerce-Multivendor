package com.abs.app.application.user.cart.dto;

import com.abs.app.application.publicapi.product.dto.ProductResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponseDto {
    private Long id;
    private ProductResponseDto product;
    private String size;
    private Integer quantity;
    private Integer mrpPrice;
    private Integer sellingPrice;
    private String userId;
}

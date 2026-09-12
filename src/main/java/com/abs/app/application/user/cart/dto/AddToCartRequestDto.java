package com.abs.app.application.user.cart.dto;

import com.abs.app.common.constant.CartConstant;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddToCartRequestDto {
    @NotBlank(message = CartConstant.PRODUCT_ID_REQUIRED)
    private String productId;

    @NotNull(message = "SKU ID required")
    private Long skuId;

    @NotNull(message = CartConstant.QUANTITY_REQUIRED)
    @Min(value = 1, message = CartConstant.QUANTITY_MIN)
    private Integer quantity;
}

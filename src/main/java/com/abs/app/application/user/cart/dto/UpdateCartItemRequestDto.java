package com.abs.app.application.user.cart.dto;

import com.abs.app.common.constant.CartConstant;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCartItemRequestDto {
    @NotNull(message = CartConstant.QUANTITY_REQUIRED)
    @Min(value = 1, message = CartConstant.QUANTITY_MIN)
    private Integer quantity;
}

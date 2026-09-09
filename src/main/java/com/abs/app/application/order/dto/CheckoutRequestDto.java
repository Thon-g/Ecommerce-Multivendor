package com.abs.app.application.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckoutRequestDto {
    @NotNull(message = "Địa chỉ giao hàng không được để trống")
    private Long addressId;
}

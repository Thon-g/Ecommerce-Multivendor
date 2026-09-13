package com.abs.app.application.order.dto;

import com.abs.app.common.constant.OrderConstant;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckoutRequestDto {
    @NotNull(message = OrderConstant.ADDRESS_NOT_NULL)
    private Long addressId;
}

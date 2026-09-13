package com.abs.app.application.order.dto;

import com.abs.app.common.constant.OrderConstant;
import com.abs.app.domain.entity.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusRequestDto {
    @NotNull(message = OrderConstant.ORDER_STATUS_REQUIRED)
    private OrderStatus orderStatus;
}

package com.abs.app.application.order.command;

import com.abs.app.domain.entity.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusCommand {
    private String orderId;
    private String userId;
    private OrderStatus orderStatus;
}

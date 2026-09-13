package com.abs.app.application.order.dto;

import com.abs.app.domain.entity.enums.OrderStatus;
import com.abs.app.domain.entity.enums.PaymentStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDto {
    private String orderId;
    private String sellerId;
    private List<OrderItemResponseDto> orderItems;
    private Double totalMrpPrice;
    private Integer totalSellingPrice;
    private Integer discount;
    private OrderStatus orderStatus;
    private Integer totalItem;
    private PaymentStatus paymentStatus;
    private LocalDateTime orderDate;
    private LocalDateTime deliverDate;
    private String cancelReason;
}

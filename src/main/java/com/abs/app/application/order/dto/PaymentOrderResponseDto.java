package com.abs.app.application.order.dto;

import com.abs.app.domain.entity.enums.PaymentMethod;
import com.abs.app.domain.entity.enums.PaymentOrderStatus;
import lombok.Data;

import java.util.List;

@Data
public class PaymentOrderResponseDto {
    private Long id;
    private Long amount;
    private PaymentOrderStatus status;
    private PaymentMethod paymentMethod;
    private List<OrderResponseDto> orders;
}

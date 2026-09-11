package com.abs.app.application.order.dto;

import com.abs.app.common.constant.OrderConstant;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CancelOrderRequestDto {
    @NotBlank(message = OrderConstant.CANCEL_REASON_REQUIRED)
    private String cancelReason;
}

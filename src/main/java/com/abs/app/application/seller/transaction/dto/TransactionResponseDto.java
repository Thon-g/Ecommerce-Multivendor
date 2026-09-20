package com.abs.app.application.seller.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponseDto {
    private Long id;
    private String orderId;
    private String customerName;
    private Integer orderAmount;
    private LocalDateTime date;
}

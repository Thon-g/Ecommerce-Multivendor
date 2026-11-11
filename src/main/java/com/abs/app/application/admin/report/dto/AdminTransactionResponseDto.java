package com.abs.app.application.admin.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminTransactionResponseDto {
    private Long id;
    private String orderId;
    private String customerName;
    private String shopName;
    private Integer orderAmount;
    private Integer platformFee;
    private LocalDateTime date;
}

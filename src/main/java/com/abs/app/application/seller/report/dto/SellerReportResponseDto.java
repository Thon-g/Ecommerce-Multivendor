package com.abs.app.application.seller.report.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellerReportResponseDto {
    private Long totalEarnings;
    private Long totalSales;
    private Long totalRefunds;
    private Long totalTax;
    private Long netEarnings;
    private Integer totalOrder;
    private Integer canceledOrders;
    private Integer totalTransactions;
}

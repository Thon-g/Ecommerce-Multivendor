package com.abs.app.application.admin.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminReportResponseDto {
    private Long totalEarnings;
    private Long totalSales;
    private Long totalRefunds;
    private Integer totalOrders;
    private Integer canceledOrders;
    private Integer totalTransactions;
    private Long totalSellers;
    private Long totalUsers;
}

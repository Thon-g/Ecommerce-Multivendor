package com.abs.app.infrastructure.mapper;

import com.abs.app.application.admin.report.dto.AdminReportResponseDto;
import com.abs.app.application.admin.report.dto.AdminTransactionResponseDto;
import com.abs.app.domain.entity.AdminReport;
import com.abs.app.domain.entity.Transaction;

public class AdminReportMapper {

    public static AdminReportResponseDto toResponseDto(AdminReport report, long totalUsers, long totalSellers) {
        if (report == null) {
            return AdminReportResponseDto.builder()
                    .totalEarnings(0L)
                    .totalSales(0L)
                    .totalRefunds(0L)
                    .totalOrders(0)
                    .canceledOrders(0)
                    .totalTransactions(0)
                    .totalSellers(totalSellers)
                    .totalUsers(totalUsers)
                    .build();
        }

        return AdminReportResponseDto.builder()
                .totalEarnings(report.getTotalEarnings() != null ? report.getTotalEarnings() : 0L)
                .totalSales(report.getTotalSales() != null ? report.getTotalSales() : 0L)
                .totalRefunds(report.getTotalRefunds() != null ? report.getTotalRefunds() : 0L)
                .totalOrders(report.getTotalOrders() != null ? report.getTotalOrders() : 0)
                .canceledOrders(report.getCanceledOrders() != null ? report.getCanceledOrders() : 0)
                .totalTransactions(report.getTotalTransactions() != null ? report.getTotalTransactions() : 0)
                .totalSellers(totalSellers)
                .totalUsers(totalUsers)
                .build();
    }

    public static AdminTransactionResponseDto toTransactionDto(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        String customerName = "";
        if (transaction.getCustomer() != null) {
            String first = transaction.getCustomer().getFirstName() != null ? transaction.getCustomer().getFirstName() : "";
            String last = transaction.getCustomer().getLastName() != null ? transaction.getCustomer().getLastName() : "";
            customerName = (first + " " + last).trim();
            if (customerName.isEmpty()) {
                customerName = transaction.getCustomer().getUserName();
            }
        }

        String shopName = "";
        if (transaction.getSeller() != null) {
            shopName = transaction.getSeller().getShopName() != null ? transaction.getSeller().getShopName() : "";
        }

        Integer orderAmount = 0;
        String orderId = null;
        if (transaction.getOrder() != null) {
            orderId = transaction.getOrder().getOrderId();
            orderAmount = transaction.getOrder().getTotalSellingPrice();
        }

        return AdminTransactionResponseDto.builder()
                .id(transaction.getId())
                .orderId(orderId)
                .customerName(customerName)
                .shopName(shopName)
                .orderAmount(orderAmount)
                .platformFee(transaction.getTotalPlatformFee() != null ? transaction.getTotalPlatformFee() : 0)
                .date(transaction.getDate())
                .build();
    }
}

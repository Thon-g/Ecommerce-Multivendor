package com.abs.app.infrastructure.mapper;

import com.abs.app.application.seller.report.dto.SellerReportResponseDto;
import com.abs.app.domain.entity.SellerReport;

public class SellerReportMapper {

    public static SellerReportResponseDto toResponseDto(SellerReport report) {
        if (report == null) {
            return new SellerReportResponseDto(0L, 0L, 0L, 0L, 0L, 0, 0, 0);
        }
        SellerReportResponseDto dto = new SellerReportResponseDto();
        dto.setTotalEarnings(report.getTotalEarnings());
        dto.setTotalSales(report.getTotalSales());
        dto.setTotalRefunds(report.getTotalRefunds());
        dto.setTotalTax(report.getTotalTax());
        dto.setNetEarnings(report.getNetEarnings());
        dto.setTotalOrder(report.getTotalOrder());
        dto.setCanceledOrders(report.getCanceledOrders());
        dto.setTotalTransactions(report.getTotalTransactions());
        return dto;
    }
}

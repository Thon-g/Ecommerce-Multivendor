package com.abs.app.domain.repository;

import com.abs.app.domain.entity.SellerReport;

import java.util.Optional;

public interface SellerReportRepository {
    SellerReport save(SellerReport sellerReport);
    Optional<SellerReport> findBySellerId(String sellerId);
}

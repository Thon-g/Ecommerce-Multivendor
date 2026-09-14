package com.abs.app.infrastructure.persistence.jpa;

import com.abs.app.domain.entity.SellerReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SellerReportJpaRepository extends JpaRepository<SellerReport, Long> {
    @Query("SELECT sr FROM SellerReport sr WHERE sr.seller.sellerId = :sellerId")
    Optional<SellerReport> findBySellerId(@Param("sellerId") String sellerId);
}

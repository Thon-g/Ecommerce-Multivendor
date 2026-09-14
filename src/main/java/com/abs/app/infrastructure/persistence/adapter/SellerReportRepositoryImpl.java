package com.abs.app.infrastructure.persistence.adapter;

import com.abs.app.domain.entity.SellerReport;
import com.abs.app.domain.repository.SellerReportRepository;
import com.abs.app.infrastructure.persistence.jpa.SellerReportJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SellerReportRepositoryImpl implements SellerReportRepository {

    private final SellerReportJpaRepository jpaRepository;

    @Override
    public SellerReport save(SellerReport sellerReport) {
        return jpaRepository.save(sellerReport);
    }

    @Override
    public Optional<SellerReport> findBySellerId(String sellerId) {
        return jpaRepository.findBySellerId(sellerId);
    }
}

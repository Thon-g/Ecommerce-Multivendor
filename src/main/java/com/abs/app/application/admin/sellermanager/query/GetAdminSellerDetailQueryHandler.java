package com.abs.app.application.admin.sellermanager.query;

import com.abs.app.application.admin.sellermanager.dto.AdminSellerDetailResponseDto;
import com.abs.app.common.constant.SellerConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.entity.SellerReport;
import com.abs.app.domain.repository.ProductRepository;
import com.abs.app.domain.repository.SellerReportRepository;
import com.abs.app.domain.repository.SellerRepository;
import com.abs.app.infrastructure.mapper.SellerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetAdminSellerDetailQueryHandler {

    private final SellerRepository sellerRepository;
    private final SellerReportRepository sellerReportRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public AdminSellerDetailResponseDto handle(GetAdminSellerDetailQuery query) {
        Seller seller = sellerRepository.findBySellerId(query.getSellerId())
                .orElseThrow(() -> new ResourceNotFoundException(SellerConstant.SELLER_NOT_FOUND));

        SellerReport report = sellerReportRepository.findBySellerId(query.getSellerId()).orElse(null);
        long totalProducts = productRepository.countBySellerId(query.getSellerId());

        return SellerMapper.toAdminSellerDetailResponseDto(seller, report, totalProducts);
    }
}


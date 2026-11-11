package com.abs.app.application.admin.sellermanager.query;

import com.abs.app.application.seller.report.dto.SellerReportResponseDto;
import com.abs.app.common.constant.SellerConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.SellerReport;
import com.abs.app.domain.repository.SellerReportRepository;
import com.abs.app.domain.repository.SellerRepository;
import com.abs.app.infrastructure.mapper.SellerReportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetAdminSellerReportQueryHandler {

    private final SellerRepository sellerRepository;
    private final SellerReportRepository sellerReportRepository;

    @Transactional(readOnly = true)
    public SellerReportResponseDto handle(GetAdminSellerReportQuery query) {
        sellerRepository.findBySellerId(query.getSellerId())
                .orElseThrow(() -> new ResourceNotFoundException(SellerConstant.SELLER_NOT_FOUND));

        SellerReport report = sellerReportRepository.findBySellerId(query.getSellerId())
                .orElse(null);

        return SellerReportMapper.toResponseDto(report);
    }
}


package com.abs.app.application.seller.report.query;

import com.abs.app.application.seller.report.dto.SellerReportResponseDto;
import com.abs.app.common.constant.OrderConstant;
import com.abs.app.common.exception.BusinessException;
import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.entity.SellerReport;
import com.abs.app.domain.repository.SellerReportRepository;
import com.abs.app.domain.repository.SellerRepository;
import com.abs.app.infrastructure.mapper.SellerReportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetSellerReportQueryHandler {

    private final SellerRepository sellerRepository;
    private final SellerReportRepository sellerReportRepository;

    public SellerReportResponseDto handle(GetSellerReportQuery query) {
        Seller seller = sellerRepository.findByUserId(query.getUserId())
                .orElseThrow(() -> new BusinessException(OrderConstant.USER_NOT_SELLER));

        SellerReport report = sellerReportRepository.findBySellerId(seller.getSellerId())
                .orElse(null);

        return SellerReportMapper.toResponseDto(report);
    }
}

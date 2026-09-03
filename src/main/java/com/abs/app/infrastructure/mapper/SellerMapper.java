package com.abs.app.infrastructure.mapper;

import com.abs.app.application.seller.dto.SellerResponseDto;
import com.abs.app.domain.entity.Seller;


public class SellerMapper {
    private SellerMapper() {}

    public static SellerResponseDto toSellerResponseDto(Seller seller) {
        if (seller == null) {
            return null;
        }

        return SellerResponseDto.builder()
                .sellerId(seller.getSellerId())
                .businessName(seller.getBusinessDetails() != null ? seller.getBusinessDetails().getBusinessName() : null)
                .businessEmail(seller.getBusinessDetails() != null ? seller.getBusinessDetails().getBusinessEmail() : null)
                .businessPhone(seller.getBusinessDetails() != null ? seller.getBusinessDetails().getBusinessPhone() : null)
                .gstin(seller.getGstin())
                .status(seller.getStatus())
                .build();
    }
}

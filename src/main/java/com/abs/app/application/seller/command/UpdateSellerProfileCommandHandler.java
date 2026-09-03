package com.abs.app.application.seller.command;

import com.abs.app.application.seller.dto.SellerResponseDto;
import com.abs.app.common.constant.SellerConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.BusinessDetails;
import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.repository.SellerRepository;
import com.abs.app.infrastructure.mapper.SellerMapper;
import com.abs.app.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateSellerProfileCommandHandler {

    private final SellerRepository sellerRepository;

    @Transactional
    public SellerResponseDto handle(UpdateSellerProfileCommand command) {
        String userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new ResourceNotFoundException(SellerConstant.SELLER_NOT_FOUND);
        }

        Seller seller = sellerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(SellerConstant.SELLER_NOT_FOUND));

        BusinessDetails businessDetails = seller.getBusinessDetails();
        if (businessDetails == null) {
            businessDetails = new BusinessDetails();
        }
        businessDetails.setBusinessName(command.getBusinessName());
        businessDetails.setBusinessEmail(command.getBusinessEmail());
        businessDetails.setBusinessPhone(command.getBusinessPhone());
        businessDetails.setBusinessAddress(command.getBusinessAddress());
        seller.setBusinessDetails(businessDetails);
        seller.setShopName(command.getBusinessName());

        seller = sellerRepository.save(seller);
        return SellerMapper.toSellerResponseDto(seller);
    }
}

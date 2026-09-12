package com.abs.app.application.seller.profile.command;

import com.abs.app.application.seller.profile.dto.SellerResponseDto;
import com.abs.app.common.constant.SellerConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.BankDetails;
import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.repository.SellerRepository;
import com.abs.app.infrastructure.mapper.SellerMapper;
import com.abs.app.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateSellerBankCommandHandler {

    private final SellerRepository sellerRepository;

    @Transactional
    public SellerResponseDto handle(UpdateSellerBankCommand command) {
        String userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new ResourceNotFoundException(SellerConstant.SELLER_NOT_FOUND);
        }

        Seller seller = sellerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(SellerConstant.SELLER_NOT_FOUND));

        BankDetails bankDetails = seller.getBankDetails();
        if (bankDetails == null) {
            bankDetails = new BankDetails();
        }
        bankDetails.setAccountName(command.getAccountName());
        bankDetails.setAccountHolderName(command.getAccountHolderName());
        bankDetails.setIfscCode(command.getIfscCode());
        seller.setBankDetails(bankDetails);

        seller = sellerRepository.save(seller);
        return SellerMapper.toSellerResponseDto(seller);
    }
}

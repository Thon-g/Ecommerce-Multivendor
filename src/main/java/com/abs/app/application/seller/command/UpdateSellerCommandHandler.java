package com.abs.app.application.seller.command;

import com.abs.app.application.seller.dto.SellerResponseDto;
import com.abs.app.common.constant.SellerConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Address;
import com.abs.app.domain.entity.BankDetails;
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
public class UpdateSellerCommandHandler {

    private final SellerRepository sellerRepository;

    @Transactional
    public SellerResponseDto handle(UpdateSellerCommand command) {
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

        BankDetails bankDetails = seller.getBankDetails();
        if (bankDetails == null) {
            bankDetails = new BankDetails();
        }
        bankDetails.setAccountName(command.getAccountName());
        bankDetails.setAccountHolderName(command.getAccountHolderName());
        bankDetails.setIfscCode(command.getIfscCode());
        seller.setBankDetails(bankDetails);

        Address pickupAddress = seller.getPickupAddress();
        if (pickupAddress == null) {
            pickupAddress = new Address();
        }
        pickupAddress.setName(command.getPickupName());
        pickupAddress.setLocality(command.getPickupLocality());
        pickupAddress.setAddress(command.getPickupAddress());
        pickupAddress.setCity(command.getPickupCity());
        pickupAddress.setState(command.getPickupState());
        pickupAddress.setPinCode(command.getPickupPinCode());
        pickupAddress.setPhone(command.getPickupPhone());
        seller.setPickupAddress(pickupAddress);

        seller = sellerRepository.save(seller);
        return SellerMapper.toSellerResponseDto(seller);
    }
}

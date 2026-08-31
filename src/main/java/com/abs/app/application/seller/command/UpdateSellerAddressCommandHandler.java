package com.abs.app.application.seller.command;

import com.abs.app.application.seller.dto.SellerResponseDto;
import com.abs.app.common.constant.SellerConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Address;
import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.repository.SellerRepository;
import com.abs.app.infrastructure.mapper.SellerMapper;
import com.abs.app.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateSellerAddressCommandHandler {

    private final SellerRepository sellerRepository;
    private final SellerMapper sellerMapper;

    @Transactional
    public SellerResponseDto handle(UpdateSellerAddressCommand command) {
        String userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new ResourceNotFoundException(SellerConstant.SELLER_NOT_FOUND);
        }

        Seller seller = sellerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(SellerConstant.SELLER_NOT_FOUND));

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
        return sellerMapper.toSellerResponseDto(seller);
    }
}

package com.abs.app.application.seller.profile.command;

import com.abs.app.application.seller.profile.dto.SellerResponseDto;
import com.abs.app.common.constant.SellerConstant;
import com.abs.app.common.constant.UserConstant;
import com.abs.app.common.exception.BusinessException;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.common.util.GenerateIdUtil;
import com.abs.app.domain.entity.Address;
import com.abs.app.domain.entity.BankDetails;
import com.abs.app.domain.entity.BusinessDetails;
import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.entity.enums.SellerStatus;
import com.abs.app.domain.repository.SellerRepository;
import com.abs.app.domain.repository.UserRepository;
import com.abs.app.infrastructure.mapper.SellerMapper;
import com.abs.app.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegisterSellerCommandHandler {

    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;

    @Transactional
    public SellerResponseDto handle(RegisterSellerCommand command) {
        String userId = SecurityUtils.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(UserConstant.USER_NOT_EXIST));

        Optional<Seller> existingSellerOpt = sellerRepository.findByUserId(userId);

        if (existingSellerOpt.isPresent()) {
            Seller existingSeller = existingSellerOpt.get();

            if (existingSeller.getStatus() == SellerStatus.ACTIVE) {
                throw new BusinessException(SellerConstant.SELLER_ALREADY_EXISTS);
            } else if (existingSeller.getStatus() == SellerStatus.PENDING_VERIFICATION) {
                throw new BusinessException(SellerConstant.SELLER_PENDING_EXISTS);
            } else if (existingSeller.getStatus() == SellerStatus.BANNED) {
                throw new BusinessException(SellerConstant.SELLER_BANNED);
            }

            // For CLOSED, DEACTIVATED, SUSPENDED -> allow re-registration by updating existing record
            updateExistingSeller(existingSeller, command);
            existingSeller.setStatus(SellerStatus.PENDING_VERIFICATION);

            Seller savedSeller = sellerRepository.save(existingSeller);
            return SellerMapper.toSellerResponseDto(savedSeller);
        }

        String sellerId = GenerateIdUtil.GenerateId(SellerConstant.SALT_TAG, SellerConstant.STRING_LIMIT);
        Seller seller = createNewSeller(command, user, sellerId);

        Seller savedSeller = sellerRepository.save(seller);

        return SellerMapper.toSellerResponseDto(savedSeller);
    }

    private Seller createNewSeller(RegisterSellerCommand command, User user, String sellerId) {
        Seller seller = new Seller();
        seller.setSellerId(sellerId);
        seller.setUser(user);
        seller.setShopName(command.getBusinessName());
        
        BusinessDetails businessDetails = new BusinessDetails();
        businessDetails.setBusinessName(command.getBusinessName());
        businessDetails.setBusinessEmail(command.getBusinessEmail());
        businessDetails.setBusinessPhone(command.getBusinessPhone());
        businessDetails.setBusinessAddress(command.getBusinessAddress());
        seller.setBusinessDetails(businessDetails);
        
        BankDetails bankDetails = new BankDetails();
        bankDetails.setAccountName(command.getAccountName());
        bankDetails.setAccountHolderName(command.getAccountHolderName());
        bankDetails.setIfscCode(command.getIfscCode());
        seller.setBankDetails(bankDetails);
        
        Address pickupAddress = new Address();
        pickupAddress.setName(command.getPickupName());
        pickupAddress.setLocality(command.getPickupLocality());
        pickupAddress.setAddress(command.getPickupAddress());
        pickupAddress.setCity(command.getPickupCity());
        pickupAddress.setState(command.getPickupState());
        pickupAddress.setPinCode(command.getPickupPinCode());
        pickupAddress.setPhone(command.getPickupPhone());
        seller.setPickupAddress(pickupAddress);
        
        seller.setGstin(command.getGstin());
        seller.setStatus(SellerStatus.PENDING_VERIFICATION);
        
        return seller;
    }

    private void updateExistingSeller(Seller seller, RegisterSellerCommand command) {
        seller.setShopName(command.getBusinessName());

        if (seller.getBusinessDetails() == null) {
            seller.setBusinessDetails(new BusinessDetails());
        }
        seller.getBusinessDetails().setBusinessName(command.getBusinessName());
        seller.getBusinessDetails().setBusinessEmail(command.getBusinessEmail());
        seller.getBusinessDetails().setBusinessPhone(command.getBusinessPhone());
        seller.getBusinessDetails().setBusinessAddress(command.getBusinessAddress());

        if (seller.getBankDetails() == null) {
            seller.setBankDetails(new BankDetails());
        }
        seller.getBankDetails().setAccountName(command.getAccountName());
        seller.getBankDetails().setAccountHolderName(command.getAccountHolderName());
        seller.getBankDetails().setIfscCode(command.getIfscCode());

        if (seller.getPickupAddress() == null) {
            seller.setPickupAddress(new Address());
        }
        seller.getPickupAddress().setName(command.getPickupName());
        seller.getPickupAddress().setLocality(command.getPickupLocality());
        seller.getPickupAddress().setAddress(command.getPickupAddress());
        seller.getPickupAddress().setCity(command.getPickupCity());
        seller.getPickupAddress().setState(command.getPickupState());
        seller.getPickupAddress().setPinCode(command.getPickupPinCode());
        seller.getPickupAddress().setPhone(command.getPickupPhone());

        seller.setGstin(command.getGstin());
    }
}

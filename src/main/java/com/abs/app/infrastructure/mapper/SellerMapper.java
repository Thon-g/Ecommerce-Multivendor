package com.abs.app.infrastructure.mapper;

import com.abs.app.application.seller.command.RegisterSellerCommand;
import com.abs.app.application.seller.dto.RegisterSellerRequestDto;
import com.abs.app.application.seller.dto.SellerResponseDto;
import com.abs.app.domain.entity.Address;
import com.abs.app.domain.entity.BankDetails;
import com.abs.app.domain.entity.BusinessDetails;
import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.entity.enums.SellerStatus;
import org.springframework.stereotype.Component;

@Component
public class SellerMapper {

    public RegisterSellerCommand toCommand(RegisterSellerRequestDto dto) {
        if (dto == null) return null;
        return new RegisterSellerCommand(
                dto.getBusinessName(),
                dto.getBusinessEmail(),
                dto.getBusinessPhone(),
                dto.getBusinessAddress(),
                dto.getAccountName(),
                dto.getAccountHolderName(),
                dto.getIfscCode(),
                dto.getPickupName(),
                dto.getPickupLocality(),
                dto.getPickupAddress(),
                dto.getPickupCity(),
                dto.getPickupState(),
                dto.getPickupPinCode(),
                dto.getPickupPhone(),
                dto.getGstin()
        );
    }

    public Seller toSeller(RegisterSellerCommand command, User user, String sellerId) {
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

    public SellerResponseDto toSellerResponseDto(Seller seller) {
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

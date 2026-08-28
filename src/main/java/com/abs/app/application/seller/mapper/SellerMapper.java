package com.abs.app.application.seller.mapper;

import com.abs.app.application.seller.command.RegisterSellerCommand;
import com.abs.app.domain.entity.Address;
import com.abs.app.domain.entity.BankDetails;
import com.abs.app.domain.entity.BusinessDetails;
import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.entity.enums.AccountStatus;
import org.springframework.stereotype.Component;

@Component
public class SellerMapper {

    public Seller toSeller(RegisterSellerCommand command, User user, String sellerId) {
        Seller seller = new Seller();
        seller.setSellerId(sellerId);
        seller.setUser(user);
        
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
        seller.setSellerStatus(AccountStatus.PENDING_VERIFICATION);
        
        return seller;
    }
}

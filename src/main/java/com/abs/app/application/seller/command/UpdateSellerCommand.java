package com.abs.app.application.seller.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateSellerCommand {
    private final String businessName;
    private final String businessEmail;
    private final String businessPhone;
    private final String businessAddress;
    
    private final String accountName;
    private final String accountHolderName;
    private final String ifscCode;
    
    private final String pickupName;
    private final String pickupLocality;
    private final String pickupAddress;
    private final String pickupCity;
    private final String pickupState;
    private final String pickupPinCode;
    private final String pickupPhone;
}

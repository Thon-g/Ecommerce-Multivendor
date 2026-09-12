package com.abs.app.application.seller.profile.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterSellerCommand {
    private String businessName;
    private String businessEmail;
    private String businessPhone;
    private String businessAddress;

    private String accountName;
    private String accountHolderName;
    private String ifscCode;

    private String pickupName;
    private String pickupLocality;
    private String pickupAddress;
    private String pickupCity;
    private String pickupState;
    private String pickupPinCode;
    private String pickupPhone;

    private String gstin;
}

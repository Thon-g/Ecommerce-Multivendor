package com.abs.app.application.seller.profile.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateSellerAddressCommand {
    private final String pickupName;
    private final String pickupLocality;
    private final String pickupAddress;
    private final String pickupCity;
    private final String pickupState;
    private final String pickupPinCode;
    private final String pickupPhone;
}

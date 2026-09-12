package com.abs.app.application.seller.profile.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateSellerProfileCommand {
    private final String businessName;
    private final String businessEmail;
    private final String businessPhone;
    private final String businessAddress;
}

package com.abs.app.application.seller.profile.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateSellerBankCommand {
    private final String accountName;
    private final String accountHolderName;
    private final String ifscCode;
}

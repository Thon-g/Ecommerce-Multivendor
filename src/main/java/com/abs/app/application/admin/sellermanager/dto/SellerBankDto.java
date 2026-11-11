package com.abs.app.application.admin.sellermanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerBankDto {
    private String accountName;
    private String accountHolderName;
    private String ifscCode;
}


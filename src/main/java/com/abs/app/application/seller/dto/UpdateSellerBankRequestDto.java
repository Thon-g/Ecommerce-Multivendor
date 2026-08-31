package com.abs.app.application.seller.dto;

import com.abs.app.common.constant.SellerConstant;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSellerBankRequestDto {

    @NotBlank(message = SellerConstant.ACCOUNT_NAME_REQUIRED)
    private String accountName;

    @NotBlank(message = SellerConstant.ACCOUNT_HOLDER_NAME_REQUIRED)
    private String accountHolderName;

    @NotBlank(message = SellerConstant.IFSC_CODE_REQUIRED)
    private String ifscCode;
}

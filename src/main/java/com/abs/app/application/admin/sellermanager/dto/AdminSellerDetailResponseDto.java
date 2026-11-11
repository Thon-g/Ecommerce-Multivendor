package com.abs.app.application.admin.sellermanager.dto;

import com.abs.app.application.seller.report.dto.SellerReportResponseDto;
import com.abs.app.domain.entity.enums.SellerStatus;
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
public class AdminSellerDetailResponseDto {

    private String sellerId;
    private String shopName;
    private SellerStatus status;
    private String gstin;
    private SellerOwnerDto owner;
    private SellerBusinessDto businessDetails;
    private SellerBankDto bankDetails;
    private SellerPickupAddressDto pickupAddress;
    private SellerReportResponseDto report;
    private Long totalProducts;
}

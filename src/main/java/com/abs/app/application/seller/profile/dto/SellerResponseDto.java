package com.abs.app.application.seller.profile.dto;

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
public class SellerResponseDto {
    private String sellerId;
    private String businessName;
    private String businessEmail;
    private String businessPhone;
    private String gstin;
    private SellerStatus status;
}

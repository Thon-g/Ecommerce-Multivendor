package com.abs.app.infrastructure.mapper;

import com.abs.app.application.admin.sellermanager.dto.*;
import com.abs.app.application.seller.profile.dto.SellerResponseDto;
import com.abs.app.domain.entity.*;

public class SellerMapper {
    private SellerMapper() {}

    public static SellerResponseDto toSellerResponseDto(Seller seller) {
        if (seller == null) {
            return null;
        }

        return SellerResponseDto.builder()
                .sellerId(seller.getSellerId())
                .shopName(seller.getShopName())
                .businessName(seller.getBusinessDetails() != null ? seller.getBusinessDetails().getBusinessName() : null)
                .businessEmail(seller.getBusinessDetails() != null ? seller.getBusinessDetails().getBusinessEmail() : null)
                .businessPhone(seller.getBusinessDetails() != null ? seller.getBusinessDetails().getBusinessPhone() : null)
                .gstin(seller.getGstin())
                .status(seller.getStatus())
                .build();
    }

    public static AdminSellerDetailResponseDto toAdminSellerDetailResponseDto(Seller seller, SellerReport report, long totalProducts) {
        if (seller == null) {
            return null;
        }

        SellerOwnerDto ownerDto = null;
        User user = seller.getUser();
        if (user != null) {
            String fullName = ((user.getFirstName() != null ? user.getFirstName() : "") + " " +
                    (user.getLastName() != null ? user.getLastName() : "")).trim();
            ownerDto = SellerOwnerDto.builder()
                    .userId(user.getUserId())
                    .userName(user.getUserName())
                    .fullName(fullName.isEmpty() ? null : fullName)
                    .email(user.getEmail())
                    .phoneNumber(user.getPhoneNumber())
                    .picture(user.getPicture())
                    .status(user.getStatus())
                    .createdAt(user.getCreatedAt())
                    .build();
        }

        SellerBusinessDto businessDto = null;
        BusinessDetails businessDetails = seller.getBusinessDetails();
        if (businessDetails != null) {
            businessDto = SellerBusinessDto.builder()
                    .businessName(businessDetails.getBusinessName())
                    .businessEmail(businessDetails.getBusinessEmail())
                    .businessPhone(businessDetails.getBusinessPhone())
                    .businessAddress(businessDetails.getBusinessAddress())
                    .logo(businessDetails.getLogo())
                    .banner(businessDetails.getBanner())
                    .build();
        }

        SellerBankDto bankDto = null;
        BankDetails bankDetails = seller.getBankDetails();
        if (bankDetails != null) {
            bankDto = SellerBankDto.builder()
                    .accountName(bankDetails.getAccountName())
                    .accountHolderName(bankDetails.getAccountHolderName())
                    .ifscCode(bankDetails.getIfscCode())
                    .build();
        }

        SellerPickupAddressDto addressDto = null;
        Address address = seller.getPickupAddress();
        if (address != null) {
            addressDto = SellerPickupAddressDto.builder()
                    .id(address.getId())
                    .name(address.getName())
                    .locality(address.getLocality())
                    .address(address.getAddress())
                    .city(address.getCity())
                    .state(address.getState())
                    .pinCode(address.getPinCode())
                    .phone(address.getPhone())
                    .build();
        }

        return AdminSellerDetailResponseDto.builder()
                .sellerId(seller.getSellerId())
                .shopName(seller.getShopName())
                .status(seller.getStatus())
                .gstin(seller.getGstin())
                .owner(ownerDto)
                .businessDetails(businessDto)
                .bankDetails(bankDto)
                .pickupAddress(addressDto)
                .report(SellerReportMapper.toResponseDto(report))
                .totalProducts(totalProducts)
                .build();
    }
}

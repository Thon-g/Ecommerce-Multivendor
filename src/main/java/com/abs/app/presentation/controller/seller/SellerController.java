package com.abs.app.presentation.controller.seller;

import com.abs.app.application.seller.profile.command.*;
import com.abs.app.application.seller.profile.dto.*;
import com.abs.app.application.seller.profile.query.GetCurrentSellerQuery;
import com.abs.app.application.seller.profile.query.GetCurrentSellerQueryHandler;
import com.abs.app.common.constant.SellerConstant;
import com.abs.app.common.response.ApiResponse;
import com.abs.app.infrastructure.mapper.SellerMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
@RequiredArgsConstructor
public class SellerController {

    private final RegisterSellerCommandHandler registerSellerCommandHandler;
    private final UpdateSellerCommandHandler updateSellerCommandHandler;
    private final UpdateSellerProfileCommandHandler updateSellerProfileCommandHandler;
    private final UpdateSellerBankCommandHandler updateSellerBankCommandHandler;
    private final UpdateSellerAddressCommandHandler updateSellerAddressCommandHandler;
    private final GetCurrentSellerQueryHandler getCurrentSellerQueryHandler;

    @PostMapping("/register")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<SellerResponseDto>> registerSeller(@Valid @RequestBody RegisterSellerRequestDto request) {
        RegisterSellerCommand command = new RegisterSellerCommand(
                request.getBusinessName(),
                request.getBusinessEmail(),
                request.getBusinessPhone(),
                request.getBusinessAddress(),
                request.getAccountName(),
                request.getAccountHolderName(),
                request.getIfscCode(),
                request.getPickupName(),
                request.getPickupLocality(),
                request.getPickupAddress(),
                request.getPickupCity(),
                request.getPickupState(),
                request.getPickupPinCode(),
                request.getPickupPhone(),
                request.getGstin()
        );
        SellerResponseDto responseDto = registerSellerCommandHandler.handle(command);
        return ResponseEntity.ok(new ApiResponse<>(true, SellerConstant.SELLER_REGISTER_SUCCESS, responseDto));
    }

    @GetMapping("/current")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<SellerResponseDto>> getCurrentSeller() {
        SellerResponseDto responseDto = getCurrentSellerQueryHandler.handle(new GetCurrentSellerQuery());
        return ResponseEntity.ok(new ApiResponse<>(true, SellerConstant.GET_CURRENT_SELLER_SUCCESS, responseDto));
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<SellerResponseDto>> updateSeller(@Valid @RequestBody UpdateSellerRequestDto request) {
        UpdateSellerCommand command = new UpdateSellerCommand(
                request.getBusinessName(), request.getBusinessEmail(), request.getBusinessPhone(), request.getBusinessAddress(),
                request.getAccountName(), request.getAccountHolderName(), request.getIfscCode(),
                request.getPickupName(), request.getPickupLocality(), request.getPickupAddress(), request.getPickupCity(),
                request.getPickupState(), request.getPickupPinCode(), request.getPickupPhone()
        );
        SellerResponseDto responseDto = updateSellerCommandHandler.handle(command);
        return ResponseEntity.ok(new ApiResponse<>(true, SellerConstant.SELLER_PROFILE_UPDATED, responseDto));
    }

    @PatchMapping("/profile")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<SellerResponseDto>> updateProfile(@Valid @RequestBody UpdateSellerProfileRequestDto request) {
        UpdateSellerProfileCommand command = new UpdateSellerProfileCommand(
                request.getBusinessName(), request.getBusinessEmail(), request.getBusinessPhone(), request.getBusinessAddress()
        );
        SellerResponseDto responseDto = updateSellerProfileCommandHandler.handle(command);
        return ResponseEntity.ok(new ApiResponse<>(true, SellerConstant.SELLER_PROFILE_UPDATED, responseDto));
    }

    @PatchMapping("/bank")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<SellerResponseDto>> updateBank(@Valid @RequestBody UpdateSellerBankRequestDto request) {
        UpdateSellerBankCommand command = new UpdateSellerBankCommand(
                request.getAccountName(), request.getAccountHolderName(), request.getIfscCode()
        );
        SellerResponseDto responseDto = updateSellerBankCommandHandler.handle(command);
        return ResponseEntity.ok(new ApiResponse<>(true, SellerConstant.SELLER_BANK_UPDATED, responseDto));
    }

    @PatchMapping("/address")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<SellerResponseDto>> updateAddress(@Valid @RequestBody UpdateSellerAddressRequestDto request) {
        UpdateSellerAddressCommand command = new UpdateSellerAddressCommand(
                request.getPickupName(), request.getPickupLocality(), request.getPickupAddress(), request.getPickupCity(),
                request.getPickupState(), request.getPickupPinCode(), request.getPickupPhone()
        );
        SellerResponseDto responseDto = updateSellerAddressCommandHandler.handle(command);
        return ResponseEntity.ok(new ApiResponse<>(true, SellerConstant.SELLER_ADDRESS_UPDATED, responseDto));
    }
}

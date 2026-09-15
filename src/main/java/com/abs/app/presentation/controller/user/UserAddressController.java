package com.abs.app.presentation.controller.user;

import com.abs.app.application.user.address.command.*;
import com.abs.app.application.user.address.dto.AddressRequestDto;
import com.abs.app.application.user.address.dto.AddressResponseDto;
import com.abs.app.application.user.address.query.GetUserAddressesQueryHandler;
import com.abs.app.common.constant.AddressConstant;
import com.abs.app.common.response.ApiResponse;
import com.abs.app.infrastructure.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/addresses")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class UserAddressController {
    private final CreateAddressCommandHandler createAddressCommandHandler;
    private final UpdateAddressCommandHandler updateAddressCommandHandler;
    private final DeleteAddressCommandHandler deleteAddressCommandHandler;
    private final GetUserAddressesQueryHandler getUserAddressesQueryHandler;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponseDto>>> getUserAddresses() {
        String userId = SecurityUtils.getCurrentUserId();
        List<AddressResponseDto> addresses = getUserAddressesQueryHandler.handle(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, AddressConstant.ADDRESS_FETCHED_SUCCESS, addresses));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createAddress(@Valid @RequestBody AddressRequestDto dto) {
        String userId = SecurityUtils.getCurrentUserId();
        createAddressCommandHandler.handle(new CreateAddressCommand(userId, dto));
        return ResponseEntity.ok(new ApiResponse<>(true, AddressConstant.ADDRESS_CREATED_SUCCESS, null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateAddress(@PathVariable Long id, @Valid @RequestBody AddressRequestDto dto) {
        String userId = SecurityUtils.getCurrentUserId();
        updateAddressCommandHandler.handle(new UpdateAddressCommand(userId, id, dto));
        return ResponseEntity.ok(new ApiResponse<>(true, AddressConstant.ADDRESS_UPDATED_SUCCESS, null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(@PathVariable Long id) {
        String userId = SecurityUtils.getCurrentUserId();
        deleteAddressCommandHandler.handle(new DeleteAddressCommand(userId, id));
        return ResponseEntity.ok(new ApiResponse<>(true, AddressConstant.ADDRESS_DELETED_SUCCESS, null));
    }
}

package com.abs.app.presentation.controller.seller;

import com.abs.app.application.seller.command.RegisterSellerCommand;
import com.abs.app.application.seller.command.RegisterSellerCommandHandler;
import com.abs.app.application.seller.dto.RegisterSellerRequestDto;
import com.abs.app.application.seller.dto.SellerResponseDto;
import com.abs.app.common.constant.SellerConstant;
import com.abs.app.common.response.ApiResponse;
import com.abs.app.infrastructure.mapper.SellerMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
@RequiredArgsConstructor
public class SellerController {

    private final RegisterSellerCommandHandler registerSellerCommandHandler;
    private final SellerMapper sellerMapper;

    @PostMapping("/register")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<SellerResponseDto>> registerSeller(@Valid @RequestBody RegisterSellerRequestDto request) {
        RegisterSellerCommand command = sellerMapper.toCommand(request);
        SellerResponseDto responseDto = registerSellerCommandHandler.handle(command);
        return ResponseEntity.ok(new ApiResponse<>(true, SellerConstant.SELLER_REGISTER_SUCCESS, responseDto));
    }
}

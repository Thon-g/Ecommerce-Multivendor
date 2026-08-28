package com.abs.app.presentation.controller.seller;

import com.abs.app.application.seller.command.RegisterSellerCommand;
import com.abs.app.application.seller.command.RegisterSellerCommandHandler;
import com.abs.app.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Seller", description = "Seller Onboarding & Management APIs")
@SecurityRequirement(name = "bearerAuth")
public class SellerController {

    private final RegisterSellerCommandHandler registerSellerCommandHandler;

    @Operation(summary = "Đăng ký mở gian hàng", description = "Gửi thông tin gian hàng để chờ Admin phê duyệt. Mỗi tài khoản chỉ được mở 1 gian hàng.")
    @PostMapping("/register")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<Void>> registerSeller(@Valid @RequestBody RegisterSellerCommand command) {
        String message = registerSellerCommandHandler.handle(command);
        return ResponseEntity.ok(new ApiResponse<>(true, message, null));
    }
}

package com.abs.app.presentation.controller.admin;

import com.abs.app.application.admin.sellermanager.command.UpdateSellerAccountStatusCommand;
import com.abs.app.application.admin.sellermanager.command.UpdateSellerAccountStatusCommandHandler;
import com.abs.app.application.admin.sellermanager.query.GetAllSellersQuery;
import com.abs.app.application.admin.sellermanager.query.GetAllSellersQueryHandler;
import com.abs.app.application.seller.dto.SellerResponseDto;
import com.abs.app.common.constant.SellerConstant;
import com.abs.app.common.response.ApiResponse;
import com.abs.app.domain.entity.enums.SellerStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/sellers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminSellerController {

    private final GetAllSellersQueryHandler getAllSellersQueryHandler;
    private final UpdateSellerAccountStatusCommandHandler updateSellerAccountStatusCommandHandler;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SellerResponseDto>>> getAllSellers(
            @RequestParam(required = false) SellerStatus status) {
        
        List<SellerResponseDto> response = getAllSellersQueryHandler.handle(new GetAllSellersQuery(status));
        return ResponseEntity.ok(new ApiResponse<>(true, SellerConstant.ADMIN_GET_SELLERS_SUCCESS, response));
    }

    @PatchMapping("/{sellerId}/status")
    public ResponseEntity<ApiResponse<SellerResponseDto>> updateSellerStatus(
            @PathVariable String sellerId,
            @RequestParam SellerStatus status) {
        
        UpdateSellerAccountStatusCommand command = new UpdateSellerAccountStatusCommand(sellerId, status);
        SellerResponseDto response = updateSellerAccountStatusCommandHandler.handle(command);
        return ResponseEntity.ok(new ApiResponse<>(true, SellerConstant.ADMIN_UPDATE_SELLER_STATUS_SUCCESS, response));
    }
}

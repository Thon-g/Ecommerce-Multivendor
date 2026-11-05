package com.abs.app.presentation.controller.admin;

import com.abs.app.application.admin.banner.command.CreateDealCommand;
import com.abs.app.application.admin.banner.command.CreateDealCommandHandler;
import com.abs.app.application.admin.banner.command.DeleteDealCommand;
import com.abs.app.application.admin.banner.command.DeleteDealCommandHandler;
import com.abs.app.application.admin.banner.dto.DealRequestDto;
import com.abs.app.application.admin.banner.dto.DealResponseDto;
import com.abs.app.application.admin.banner.query.GetDealsQuery;
import com.abs.app.application.admin.banner.query.GetDealsQueryHandler;
import com.abs.app.common.constant.BannerConstant;
import com.abs.app.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/deals")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDealController {

    private final CreateDealCommandHandler createDealCommandHandler;
    private final DeleteDealCommandHandler deleteDealCommandHandler;
    private final GetDealsQueryHandler getDealsQueryHandler;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DealResponseDto>>> getDeals() {
        List<DealResponseDto> response = getDealsQueryHandler.handle(new GetDealsQuery());
        return ResponseEntity.ok(new ApiResponse<>(true, BannerConstant.DEAL_FETCHED_SUCCESS, response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DealResponseDto>> createDeal(@Valid @RequestBody DealRequestDto request) {
        CreateDealCommand command = new CreateDealCommand(
                request.getDiscount(),
                request.getHomeCategoryId()
        );
        DealResponseDto response = createDealCommandHandler.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, BannerConstant.DEAL_CREATED_SUCCESS, response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDeal(@PathVariable Long id) {
        deleteDealCommandHandler.handle(new DeleteDealCommand(id));
        return ResponseEntity.ok(new ApiResponse<>(true, BannerConstant.DEAL_DELETED_SUCCESS, null));
    }
}


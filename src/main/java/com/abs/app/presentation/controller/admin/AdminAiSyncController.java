package com.abs.app.presentation.controller.admin;

import com.abs.app.application.admin.ai.command.SyncAiCategoriesCommandHandler;
import com.abs.app.application.admin.ai.command.SyncAiProductsCommandHandler;
import com.abs.app.application.admin.ai.command.SyncAllAiDataCommandHandler;
import com.abs.app.application.admin.ai.dto.SyncResultDto;
import com.abs.app.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/ai")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminAiSyncController {

    private final SyncAllAiDataCommandHandler syncAllAiDataCommandHandler;
    private final SyncAiProductsCommandHandler syncAiProductsCommandHandler;
    private final SyncAiCategoriesCommandHandler syncAiCategoriesCommandHandler;

    @PostMapping("/sync-all")
    public ResponseEntity<ApiResponse<SyncResultDto>> syncAll() {
        SyncResultDto result = syncAllAiDataCommandHandler.handle();
        return ResponseEntity.ok(new ApiResponse<>(result.isSuccess(), result.getMessage(), result));
    }

    @PostMapping("/sync-products")
    public ResponseEntity<ApiResponse<SyncResultDto>> syncProducts() {
        SyncResultDto result = syncAiProductsCommandHandler.handle();
        return ResponseEntity.ok(new ApiResponse<>(result.isSuccess(), result.getMessage(), result));
    }

    @PostMapping("/sync-categories")
    public ResponseEntity<ApiResponse<SyncResultDto>> syncCategories() {
        SyncResultDto result = syncAiCategoriesCommandHandler.handle();
        return ResponseEntity.ok(new ApiResponse<>(result.isSuccess(), result.getMessage(), result));
    }
}

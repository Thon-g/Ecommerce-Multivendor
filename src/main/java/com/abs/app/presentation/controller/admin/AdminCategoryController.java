package com.abs.app.presentation.controller.admin;

import com.abs.app.application.admin.category.command.*;
import com.abs.app.application.admin.category.dto.CategoryResponseDto;
import com.abs.app.application.admin.category.dto.CreateCategoryRequestDto;
import com.abs.app.application.admin.category.dto.UpdateCategoryRequestDto;
import com.abs.app.common.constant.CategoryConstant;
import com.abs.app.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCategoryController {

    private final CreateCategoryCommandHandler createCategoryCommandHandler;
    private final UpdateCategoryCommandHandler updateCategoryCommandHandler;
    private final DeleteCategoryCommandHandler deleteCategoryCommandHandler;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponseDto>> createCategory(@Valid @RequestBody CreateCategoryRequestDto request) {
        CreateCategoryCommand command = new CreateCategoryCommand(
                request.getName(),
                request.getCategoryId(),
                request.getParentCategoryId(),
                request.getLevel()
        );
        CategoryResponseDto response = createCategoryCommandHandler.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, CategoryConstant.CATEGORY_CREATED_SUCCESS, response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> updateCategory(
            @PathVariable String id,
            @Valid @RequestBody UpdateCategoryRequestDto request) {
        
        UpdateCategoryCommand command = new UpdateCategoryCommand(id, request.getName());
        CategoryResponseDto response = updateCategoryCommandHandler.handle(command);
        return ResponseEntity.ok(new ApiResponse<>(true, CategoryConstant.CATEGORY_UPDATED_SUCCESS, response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable String id) {
        deleteCategoryCommandHandler.handle(new DeleteCategoryCommand(id));
        return ResponseEntity.ok(new ApiResponse<>(true, CategoryConstant.CATEGORY_DELETED_SUCCESS, null));
    }
}

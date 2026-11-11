package com.abs.app.presentation.controller.admin;

import com.abs.app.application.admin.category.command.*;
import com.abs.app.application.admin.category.dto.CreateCategoryRequestDto;
import com.abs.app.application.admin.category.dto.UpdateCategoryRequestDto;
import com.abs.app.application.admin.category.query.GetAdminCategoriesPagedQuery;
import com.abs.app.application.admin.category.query.GetAdminCategoriesPagedQueryHandler;
import com.abs.app.application.admin.category.query.GetAdminCategoryDetailQuery;
import com.abs.app.application.admin.category.query.GetAdminCategoryDetailQueryHandler;
import com.abs.app.application.publicapi.category.dto.CategoryResponseDto;
import com.abs.app.application.publicapi.category.dto.CategoryTreeResponseDto;
import com.abs.app.common.constant.CategoryConstant;
import com.abs.app.common.response.ApiResponse;
import com.abs.app.common.response.PageResponse;
import com.abs.app.domain.service.CategoryTreeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCategoryController {

    private final CreateCategoryCommandHandler createCategoryCommandHandler;
    private final UpdateCategoryCommandHandler updateCategoryCommandHandler;
    private final DeleteCategoryCommandHandler deleteCategoryCommandHandler;
    private final GetAdminCategoriesPagedQueryHandler getAdminCategoriesPagedQueryHandler;
    private final GetAdminCategoryDetailQueryHandler getAdminCategoryDetailQueryHandler;
    private final CategoryTreeService categoryTreeService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CategoryResponseDto>>> getCategories(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer level,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<CategoryResponseDto> response = getAdminCategoriesPagedQueryHandler.handle(
                new GetAdminCategoriesPagedQuery(keyword, level, page, size)
        );
        return ResponseEntity.ok(new ApiResponse<>(true, CategoryConstant.ADMIN_GET_CATEGORIES_SUCCESS, response));
    }

    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<List<CategoryTreeResponseDto>>> getCategoryTree() {
        List<CategoryTreeResponseDto> response = categoryTreeService.getCategoryTree();
        return ResponseEntity.ok(new ApiResponse<>(true, CategoryConstant.CATEGORY_FETCHED_SUCCESS, response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> getCategoryDetail(@PathVariable String id) {
        CategoryResponseDto response = getAdminCategoryDetailQueryHandler.handle(new GetAdminCategoryDetailQuery(id));
        return ResponseEntity.ok(new ApiResponse<>(true, CategoryConstant.ADMIN_GET_CATEGORY_DETAIL_SUCCESS, response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponseDto>> createCategory(@Valid @RequestBody CreateCategoryRequestDto request) {
        CreateCategoryCommand command = new CreateCategoryCommand(
                request.getName(),
                request.getCategoryId(),
                request.getParentCategoryId(),
                request.getCommissionRate()
        );
        CategoryResponseDto response = createCategoryCommandHandler.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, CategoryConstant.CATEGORY_CREATED_SUCCESS, response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> updateCategory(
            @PathVariable String id,
            @Valid @RequestBody UpdateCategoryRequestDto request) {
        UpdateCategoryCommand command = new UpdateCategoryCommand(id, request.getName(), request.getCommissionRate());
        CategoryResponseDto response = updateCategoryCommandHandler.handle(command);
        return ResponseEntity.ok(new ApiResponse<>(true, CategoryConstant.CATEGORY_UPDATED_SUCCESS, response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable String id) {
        deleteCategoryCommandHandler.handle(new DeleteCategoryCommand(id));
        return ResponseEntity.ok(new ApiResponse<>(true, CategoryConstant.CATEGORY_DELETED_SUCCESS, null));
    }
}

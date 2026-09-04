package com.abs.app.presentation.controller.category;

import com.abs.app.application.admin.category.dto.CategoryResponseDto;
import com.abs.app.application.admin.category.query.GetAllCategoriesQuery;
import com.abs.app.application.admin.category.query.GetAllCategoriesQueryHandler;
import com.abs.app.application.admin.category.query.GetCategoryByIdQuery;
import com.abs.app.application.admin.category.query.GetCategoryByIdQueryHandler;
import com.abs.app.common.constant.CategoryConstant;
import com.abs.app.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.abs.app.common.response.PageResponse;

import java.util.List;

@RestController
@RequestMapping("/public/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final GetAllCategoriesQueryHandler getAllCategoriesQueryHandler;
    private final GetCategoryByIdQueryHandler getCategoryByIdQueryHandler;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CategoryResponseDto>>> getAllCategories(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<CategoryResponseDto> response = getAllCategoriesQueryHandler.handle(new GetAllCategoriesQuery(keyword, page, size));
        return ResponseEntity.ok(new ApiResponse<>(true, CategoryConstant.CATEGORIES_FETCHED_SUCCESS, response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> getCategoryById(@PathVariable String id) {
        CategoryResponseDto response = getCategoryByIdQueryHandler.handle(new GetCategoryByIdQuery(id));
        return ResponseEntity.ok(new ApiResponse<>(true, CategoryConstant.CATEGORY_FETCHED_SUCCESS, response));
    }
}

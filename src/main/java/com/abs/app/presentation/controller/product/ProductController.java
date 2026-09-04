package com.abs.app.presentation.controller.product;

import com.abs.app.application.seller.product.dto.ProductResponseDto;
import com.abs.app.application.seller.product.query.GetAllProductsQuery;
import com.abs.app.application.seller.product.query.GetAllProductsQueryHandler;
import com.abs.app.application.seller.product.query.GetProductByIdQuery;
import com.abs.app.application.seller.product.query.GetProductByIdQueryHandler;
import com.abs.app.common.constant.ProductConstant;
import com.abs.app.common.response.ApiResponse;
import com.abs.app.common.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public/products")
@RequiredArgsConstructor
public class ProductController {

    private final GetAllProductsQueryHandler getAllProductsQueryHandler;
    private final GetProductByIdQueryHandler getProductByIdQueryHandler;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductResponseDto>>> getAllProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        PageResponse<ProductResponseDto> response = getAllProductsQueryHandler.handle(new GetAllProductsQuery(keyword, categoryId, page, size));
        return ResponseEntity.ok(new ApiResponse<>(true, ProductConstant.PRODUCTS_FETCHED_SUCCESS, response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDto>> getProductById(@PathVariable String id) {
        ProductResponseDto response = getProductByIdQueryHandler.handle(new GetProductByIdQuery(id));
        return ResponseEntity.ok(new ApiResponse<>(true, ProductConstant.PRODUCT_FETCHED_SUCCESS, response));
    }
}

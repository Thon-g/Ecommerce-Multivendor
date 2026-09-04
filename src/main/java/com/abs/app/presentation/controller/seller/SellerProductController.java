package com.abs.app.presentation.controller.seller;

import com.abs.app.application.seller.product.command.*;
import com.abs.app.application.seller.product.dto.CreateProductRequestDto;
import com.abs.app.application.publicapi.product.dto.ProductResponseDto;
import com.abs.app.application.seller.product.dto.UpdateProductRequestDto;
import com.abs.app.application.seller.product.query.GetSellerProductsQuery;
import com.abs.app.application.seller.product.query.GetSellerProductsQueryHandler;
import com.abs.app.common.constant.ProductConstant;
import com.abs.app.common.response.ApiResponse;
import com.abs.app.common.response.PageResponse;
import com.abs.app.infrastructure.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seller/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SELLER')")
public class SellerProductController {

    private final CreateProductCommandHandler createProductCommandHandler;
    private final UpdateProductCommandHandler updateProductCommandHandler;
    private final DeleteProductCommandHandler deleteProductCommandHandler;
    private final GetSellerProductsQueryHandler getSellerProductsQueryHandler;

    @PostMapping(consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProductResponseDto>> createProduct(@Valid @ModelAttribute CreateProductRequestDto request) {
        String currentUserId = SecurityUtils.getCurrentUserId();
        CreateProductCommand command = new CreateProductCommand(
                request.getTitle(),
                request.getDescription(),
                request.getMrpPrice(),
                request.getSellingPrice(),
                request.getQuantity(),
                request.getColor(),
                request.getSizes(),
                request.getImages(),
                request.getCategoryId(),
                currentUserId
        );
        ProductResponseDto response = createProductCommandHandler.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, ProductConstant.PRODUCT_CREATED_SUCCESS, response));
    }

    @PutMapping(value = "/{id}", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProductResponseDto>> updateProduct(
            @PathVariable String id,
            @Valid @ModelAttribute UpdateProductRequestDto request) {
        
        String currentUserId = SecurityUtils.getCurrentUserId();
        UpdateProductCommand command = new UpdateProductCommand(
                id,
                request.getTitle(),
                request.getDescription(),
                request.getMrpPrice(),
                request.getSellingPrice(),
                request.getQuantity(),
                request.getColor(),
                request.getSizes(),
                request.getImages(),
                request.getCategoryId(),
                currentUserId
        );
        ProductResponseDto response = updateProductCommandHandler.handle(command);
        return ResponseEntity.ok(new ApiResponse<>(true, ProductConstant.PRODUCT_UPDATED_SUCCESS, response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductResponseDto>>> getAllProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        String currentUserId = SecurityUtils.getCurrentUserId();
        
        PageResponse<ProductResponseDto> response = getSellerProductsQueryHandler.handle(new GetSellerProductsQuery(keyword, categoryId, currentUserId, page, size));
        return ResponseEntity.ok(new ApiResponse<>(true, ProductConstant.PRODUCTS_FETCHED_SUCCESS, response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable String id) {
        String currentUserId = SecurityUtils.getCurrentUserId();
        deleteProductCommandHandler.handle(new DeleteProductCommand(id, currentUserId));
        return ResponseEntity.ok(new ApiResponse<>(true, ProductConstant.PRODUCT_DELETED_SUCCESS, null));
    }
}

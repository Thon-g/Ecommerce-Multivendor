package com.abs.app.application.seller.product.query;

import com.abs.app.application.seller.product.dto.ProductResponseDto;
import com.abs.app.common.constant.ProductConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Product;
import com.abs.app.domain.repository.ProductRepository;
import com.abs.app.infrastructure.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetProductByIdQueryHandler {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public ProductResponseDto handle(GetProductByIdQuery query) {
        Product product = productRepository.findById(query.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ProductConstant.PRODUCT_NOT_FOUND));

        return ProductMapper.toProductResponseDto(product);
    }
}

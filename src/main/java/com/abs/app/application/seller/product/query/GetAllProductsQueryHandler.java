package com.abs.app.application.seller.product.query;

import com.abs.app.application.seller.product.dto.ProductResponseDto;
import com.abs.app.domain.entity.Product;
import com.abs.app.domain.repository.ProductRepository;
import com.abs.app.infrastructure.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetAllProductsQueryHandler {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<ProductResponseDto> handle(GetAllProductsQuery query) {
        List<Product> products = productRepository.findAll();
        
        // Basic filtering by categoryId if provided
        if (query.getCategoryId() != null && !query.getCategoryId().isBlank()) {
            products = products.stream()
                    .filter(p -> p.getCategory() != null && 
                            query.getCategoryId().equals(p.getCategory().getCategoryId()))
                    .collect(Collectors.toList());
        }

        return ProductMapper.toProductResponseDtoList(products);
    }
}

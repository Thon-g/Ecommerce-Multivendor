package com.abs.app.application.publicapi.product.query;

import com.abs.app.application.publicapi.product.dto.ProductResponseDto;
import com.abs.app.common.response.PageResponse;
import com.abs.app.common.util.PaginationUtil;
import com.abs.app.domain.entity.Product;
import com.abs.app.domain.repository.ProductRepository;
import com.abs.app.infrastructure.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetAllProductsQueryHandler {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public PageResponse<ProductResponseDto> handle(GetAllProductsQuery query) {
        Pageable pageable = PaginationUtil.createPageable(query.getPage(), query.getSize(), Sort.by("createAt").descending());
        
        // sellerId is null for public API
        Page<Product> page = productRepository.search(query.getKeyword(), query.getCategoryId(), null, pageable);

        return PaginationUtil.toPageResponse(
                page,
                ProductMapper::toProductResponseDto,
                query.getPage(),
                query.getSize()
        );
    }
}

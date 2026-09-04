package com.abs.app.application.seller.product.query;

import com.abs.app.application.seller.product.dto.ProductResponseDto;
import com.abs.app.common.response.PageResponse;
import com.abs.app.common.util.PaginationUtil;
import com.abs.app.domain.entity.Product;
import com.abs.app.domain.repository.ProductRepository;
import com.abs.app.infrastructure.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import com.abs.app.common.constant.SellerConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.repository.SellerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetSellerProductsQueryHandler {

    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;

    @Transactional(readOnly = true)
    public PageResponse<ProductResponseDto> handle(GetSellerProductsQuery query) {
        Seller seller = sellerRepository.findByUserId(query.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(SellerConstant.SELLER_NOT_FOUND));

        Pageable pageable = PaginationUtil.createPageable(query.getPage(), query.getSize(), Sort.by("createAt").descending());
        
        Page<Product> page = productRepository.search(query.getKeyword(), query.getCategoryId(), seller.getSellerId(), pageable);

        return PaginationUtil.toPageResponse(
                page,
                ProductMapper::toProductResponseDto,
                query.getPage(),
                query.getSize()
        );
    }
}

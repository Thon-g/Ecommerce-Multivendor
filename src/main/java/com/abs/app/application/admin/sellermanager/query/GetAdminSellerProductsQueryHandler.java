package com.abs.app.application.admin.sellermanager.query;

import com.abs.app.application.publicapi.product.dto.ProductResponseDto;
import com.abs.app.common.constant.SellerConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.common.response.PageResponse;
import com.abs.app.common.util.PaginationUtil;
import com.abs.app.domain.entity.Product;
import com.abs.app.domain.repository.ProductRepository;
import com.abs.app.domain.repository.SellerRepository;
import com.abs.app.infrastructure.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetAdminSellerProductsQueryHandler {

    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public PageResponse<ProductResponseDto> handle(GetAdminSellerProductsQuery query) {
        sellerRepository.findBySellerId(query.getSellerId())
                .orElseThrow(() -> new ResourceNotFoundException(SellerConstant.SELLER_NOT_FOUND));

        Pageable pageable = PaginationUtil.createPageable(query.getPage(), query.getSize(), Sort.by("id").descending());
        Page<Product> productPage = productRepository.findBySellerIdAndKeyword(query.getSellerId(), query.getKeyword(), pageable);

        return PaginationUtil.toPageResponse(
                productPage,
                ProductMapper::toProductResponseDto,
                query.getPage(),
                query.getSize()
        );
    }
}


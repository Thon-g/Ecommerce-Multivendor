package com.abs.app.application.admin.sellermanager.query;

import com.abs.app.application.seller.profile.dto.SellerResponseDto;
import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.repository.SellerRepository;
import com.abs.app.infrastructure.mapper.SellerMapper;
import lombok.RequiredArgsConstructor;
import com.abs.app.common.response.PageResponse;
import com.abs.app.common.util.PaginationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAllSellersQueryHandler {

    private final SellerRepository sellerRepository;

    public PageResponse<SellerResponseDto> handle(GetAllSellersQuery query) {
        Pageable pageable = PaginationUtil.createPageable(query.getPage(), query.getSize(), Sort.by("sellerId").descending());
        
        Page<Seller> page = sellerRepository.search(query.getKeyword(), query.getStatus(), pageable);

        return PaginationUtil.toPageResponse(
                page,
                SellerMapper::toSellerResponseDto,
                query.getPage(),
                query.getSize()
        );
    }
}

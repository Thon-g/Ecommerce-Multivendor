package com.abs.app.application.admin.sellermanager.query;

import com.abs.app.application.seller.dto.SellerResponseDto;
import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.repository.SellerRepository;
import com.abs.app.infrastructure.mapper.SellerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetAllSellersQueryHandler {

    private final SellerRepository sellerRepository;
    private final SellerMapper sellerMapper;

    public List<SellerResponseDto> handle(GetAllSellersQuery query) {
        List<Seller> sellers;
        
        if (query.getStatus() != null) {
            sellers = sellerRepository.findByStatus(query.getStatus());
        } else {
            sellers = sellerRepository.findAll();
        }

        return sellers.stream()
                .map(sellerMapper::toSellerResponseDto)
                .collect(Collectors.toList());
    }
}

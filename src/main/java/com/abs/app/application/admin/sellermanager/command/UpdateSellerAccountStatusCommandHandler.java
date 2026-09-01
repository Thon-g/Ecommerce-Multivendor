package com.abs.app.application.admin.sellermanager.command;

import com.abs.app.application.seller.dto.SellerResponseDto;
import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.service.SellerService;
import com.abs.app.infrastructure.mapper.SellerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateSellerAccountStatusCommandHandler {

    private final SellerService sellerService;
    private final SellerMapper sellerMapper;

    public SellerResponseDto handle(UpdateSellerAccountStatusCommand command) {
        Seller seller = sellerService.updateSellerStatus(command.getSellerId(), command.getStatus());
        
        return sellerMapper.toSellerResponseDto(seller);
    }
}

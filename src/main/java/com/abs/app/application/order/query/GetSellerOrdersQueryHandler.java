package com.abs.app.application.order.query;

import com.abs.app.application.order.dto.OrderResponseDto;
import com.abs.app.domain.entity.Order;
import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.repository.OrderRepository;
import com.abs.app.domain.repository.SellerRepository;
import com.abs.app.infrastructure.mapper.OrderMapper;
import com.abs.app.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import com.abs.app.common.constant.OrderConstant;

@Service
@RequiredArgsConstructor
public class GetSellerOrdersQueryHandler {

    private final OrderRepository orderRepository;
    private final SellerRepository sellerRepository;

    @Transactional(readOnly = true)
    public List<OrderResponseDto> handle(GetSellerOrdersQuery query) {
        Seller seller = sellerRepository.findByUserId(query.getUserId())
                .orElseThrow(() -> new BusinessException(OrderConstant.USER_NOT_SELLER));

        List<Order> orders = orderRepository.findBySellerId(seller.getSellerId());
        return orders.stream()
                .map(OrderMapper::toOrderResponseDto)
                .collect(Collectors.toList());
    }
}

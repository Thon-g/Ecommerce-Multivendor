package com.abs.app.application.order.query;

import com.abs.app.application.order.dto.OrderResponseDto;
import com.abs.app.domain.entity.Order;
import com.abs.app.domain.repository.OrderRepository;
import com.abs.app.infrastructure.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetUserOrdersQueryHandler {

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public List<OrderResponseDto> handle(GetUserOrdersQuery query) {
        List<Order> orders = orderRepository.findByUserId(query.getUserId());
        return orders.stream()
                .map(OrderMapper::toOrderResponseDto)
                .collect(Collectors.toList());
    }
}

package com.abs.app.infrastructure.persistence.adapter;

import com.abs.app.domain.entity.OrderItem;
import com.abs.app.domain.repository.OrderItemRepository;
import com.abs.app.infrastructure.persistence.jpa.OrderItemJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemRepositoryImpl implements OrderItemRepository {

    private final OrderItemJpaRepository jpaRepository;

    @Override
    public OrderItem save(OrderItem orderItem) {
        return jpaRepository.save(orderItem);
    }
}

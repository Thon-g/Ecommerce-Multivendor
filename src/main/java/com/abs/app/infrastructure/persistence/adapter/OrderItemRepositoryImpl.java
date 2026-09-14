package com.abs.app.infrastructure.persistence.adapter;

import com.abs.app.domain.entity.OrderItem;
import com.abs.app.domain.entity.enums.OrderStatus;
import com.abs.app.domain.repository.OrderItemRepository;
import com.abs.app.infrastructure.persistence.jpa.OrderItemJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderItemRepositoryImpl implements OrderItemRepository {
    private final OrderItemJpaRepository jpaRepository;

    @Override
    public OrderItem save(OrderItem orderItem) {
        return jpaRepository.save(orderItem);
    }

    @Override
    public boolean hasPurchasedProductAndDelivered(String userId, String productId) {
        return jpaRepository.existsByUserIdAndProductIdAndOrder_OrderStatus(userId, productId, OrderStatus.DELIVERED);
    }
}

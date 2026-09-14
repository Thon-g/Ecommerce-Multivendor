package com.abs.app.infrastructure.persistence.jpa;

import com.abs.app.domain.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.abs.app.domain.entity.enums.OrderStatus;

public interface OrderItemJpaRepository extends JpaRepository<OrderItem, Long> {
    boolean existsByUserIdAndProductIdAndOrder_OrderStatus(String userId, String productId, OrderStatus status);
}

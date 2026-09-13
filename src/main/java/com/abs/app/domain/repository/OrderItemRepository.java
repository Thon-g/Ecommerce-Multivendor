package com.abs.app.domain.repository;

import com.abs.app.domain.entity.OrderItem;

public interface OrderItemRepository {
    OrderItem save(OrderItem orderItem);
}

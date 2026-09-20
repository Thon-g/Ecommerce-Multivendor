package com.abs.app.domain.repository;

import com.abs.app.domain.entity.Order;
import java.util.Optional;
import java.util.List;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(String orderId);
    List<Order> findByUserId(String userId);
    List<Order> findBySellerId(String sellerId);
}

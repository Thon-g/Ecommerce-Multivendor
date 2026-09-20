package com.abs.app.infrastructure.persistence.adapter;

import com.abs.app.domain.entity.Order;
import com.abs.app.domain.repository.OrderRepository;
import com.abs.app.infrastructure.persistence.jpa.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository jpaRepository;

    @Override
    public Order save(Order order) {
        return jpaRepository.save(order);
    }

    @Override
    public Optional<Order> findById(String orderId) {
        return jpaRepository.findById(orderId);
    }

    @Override
    public List<Order> findByUserId(String userId) {
        return jpaRepository.findByUser_UserId(userId);
    }

    @Override
    public List<Order> findBySellerId(String sellerId) {
        return jpaRepository.findBySellerId(sellerId);
    }
}

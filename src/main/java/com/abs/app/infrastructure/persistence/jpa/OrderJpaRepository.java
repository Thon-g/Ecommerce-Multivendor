package com.abs.app.infrastructure.persistence.jpa;

import com.abs.app.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

public interface OrderJpaRepository extends JpaRepository<Order, String> {
    List<Order> findByUser_UserId(String userId);
    List<Order> findBySellerId(String sellerId);
}

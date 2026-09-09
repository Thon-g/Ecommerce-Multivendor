package com.abs.app.infrastructure.persistence.jpa;

import com.abs.app.domain.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface OrderItemJpaRepository extends JpaRepository<OrderItem, Long> {
}

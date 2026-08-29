package com.abs.app.infrastructure.persistence.jpa;

import com.abs.app.domain.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartJpaRepository extends JpaRepository<Cart, Long> {
}

package com.abs.app.infrastructure.persistence.jpa;

import com.abs.app.domain.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartJpaRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser_UserId(String userId);
}

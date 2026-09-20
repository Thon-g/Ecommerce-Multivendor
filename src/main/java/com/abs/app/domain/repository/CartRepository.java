package com.abs.app.domain.repository;

import com.abs.app.domain.entity.Cart;
import java.util.Optional;

public interface CartRepository {
    Optional<Cart> findByUserId(String userId);
    Cart save(Cart cart);
}

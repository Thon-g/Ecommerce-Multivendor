package com.abs.app.domain.repository;

import com.abs.app.domain.entity.CartItem;
import java.util.Optional;

public interface CartItemRepository {
    Optional<CartItem> findById(Long id);
    CartItem save(CartItem cartItem);
    void delete(CartItem cartItem);
}

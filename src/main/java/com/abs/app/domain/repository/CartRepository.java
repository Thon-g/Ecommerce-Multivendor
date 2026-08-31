package com.abs.app.domain.repository;

import com.abs.app.domain.entity.Cart;

public interface CartRepository {
    void save(Cart cart);
}

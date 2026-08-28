package com.abs.app.infrastructure.persistence.adapter;

import com.abs.app.domain.entity.Cart;
import com.abs.app.domain.repository.CartRepository;
import com.abs.app.infrastructure.persistence.jpa.CartJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CartRepositoryImpl implements CartRepository {
    private final CartJpaRepository cartJpaRepository;

    @Override
    public void save(Cart cart) {
        cartJpaRepository.save(cart);
    }
}

package com.abs.app.infrastructure.persistence;

import com.abs.app.domain.entity.CartItem;
import com.abs.app.domain.repository.CartItemRepository;
import com.abs.app.infrastructure.persistence.jpa.CartItemJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartItemRepositoryImpl implements CartItemRepository {

    private final CartItemJpaRepository cartItemJpaRepository;

    @Override
    public Optional<CartItem> findById(Long id) {
        return cartItemJpaRepository.findById(id);
    }

    @Override
    public CartItem save(CartItem cartItem) {
        return cartItemJpaRepository.save(cartItem);
    }

    @Override
    public void delete(CartItem cartItem) {
        cartItemJpaRepository.delete(cartItem);
    }
}

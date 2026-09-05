package com.abs.app.infrastructure.persistence;

import com.abs.app.domain.entity.Cart;
import com.abs.app.domain.repository.CartRepository;
import com.abs.app.infrastructure.persistence.jpa.CartJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartRepositoryImpl implements CartRepository {

    private final CartJpaRepository cartJpaRepository;

    @Override
    public Optional<Cart> findByUserId(String userId) {
        return cartJpaRepository.findByUser_UserId(userId);
    }

    @Override
    public Cart save(Cart cart) {
        return cartJpaRepository.save(cart);
    }
}

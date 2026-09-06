package com.abs.app.infrastructure.persistence.adapter;

import com.abs.app.domain.entity.Wishlist;
import com.abs.app.domain.repository.WishlistRepository;
import com.abs.app.infrastructure.persistence.jpa.WishlistJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WishlistRepositoryImpl implements WishlistRepository {
    private final WishlistJpaRepository wishlistJpaRepository;

    @Override
    public Optional<Wishlist> findByUserId(String userId) {
        return wishlistJpaRepository.findByUserUserId(userId);
    }

    @Override
    public Wishlist save(Wishlist wishlist) {
        return wishlistJpaRepository.save(wishlist);
    }
}

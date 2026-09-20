package com.abs.app.domain.repository;

import com.abs.app.domain.entity.Wishlist;
import java.util.Optional;

public interface WishlistRepository {
    Optional<Wishlist> findByUserId(String userId);
    Wishlist save(Wishlist wishlist);
}

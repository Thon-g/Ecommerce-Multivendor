package com.abs.app.infrastructure.persistence.jpa;

import com.abs.app.domain.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WishlistJpaRepository extends JpaRepository<Wishlist, Long> {
    Optional<Wishlist> findByUserUserId(String userId);
}

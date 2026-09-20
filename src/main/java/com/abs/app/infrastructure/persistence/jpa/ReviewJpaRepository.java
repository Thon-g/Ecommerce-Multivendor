package com.abs.app.infrastructure.persistence.jpa;

import com.abs.app.domain.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewJpaRepository extends JpaRepository<Review, Long> {
    Page<Review> findByProductId(String productId, Pageable pageable);
}

package com.abs.app.infrastructure.persistence.adapter;

import com.abs.app.domain.entity.Review;
import com.abs.app.domain.repository.ReviewRepository;
import com.abs.app.infrastructure.persistence.jpa.ReviewJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepository {

    private final ReviewJpaRepository jpaRepository;

    @Override
    public Review save(Review review) {
        return jpaRepository.save(review);
    }

    @Override
    public Page<Review> findByProductId(String productId, Pageable pageable) {
        return jpaRepository.findByProductId(productId, pageable);
    }
}

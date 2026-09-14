package com.abs.app.domain.repository;

import com.abs.app.domain.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewRepository {
    Review save(Review review);
    Page<Review> findByProductId(String productId, Pageable pageable);
}

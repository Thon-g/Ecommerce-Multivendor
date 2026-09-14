package com.abs.app.application.publicapi.review.command;

import com.abs.app.application.publicapi.review.dto.ReviewResponseDto;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Product;
import com.abs.app.domain.entity.Review;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.repository.OrderItemRepository;
import com.abs.app.domain.repository.ProductRepository;
import com.abs.app.domain.repository.ReviewRepository;
import com.abs.app.domain.repository.UserRepository;
import com.abs.app.infrastructure.mapper.ReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateReviewCommandHandler {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public ReviewResponseDto handle(CreateReviewCommand command) {
        // 1. Verify user exists
        User user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 2. Verify product exists
        Product product = productRepository.findById(command.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // 3. Verify user has purchased the product and order is DELIVERED
        boolean hasPurchased = orderItemRepository.hasPurchasedProductAndDelivered(command.getUserId(), command.getProductId());
        if (!hasPurchased) {
            throw new IllegalStateException("You can only review a product after purchasing and receiving it.");
        }

        // 4. Create and save Review
        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setReviewText(command.getReviewText());
        review.setRating(command.getRating());
        review.setProductImages(command.getProductImages());

        Review savedReview = reviewRepository.save(review);

        // 5. Update Product's numRatings and averageRating
        Integer currentNumRatings = product.getNumRatings() != null ? product.getNumRatings() : 0;
        Double currentAvgRating = product.getAverageRating() != null ? product.getAverageRating() : 0.0;

        Double newAvgRating = ((currentAvgRating * currentNumRatings) + command.getRating()) / (currentNumRatings + 1);
        
        product.setNumRatings(currentNumRatings + 1);
        product.setAverageRating(newAvgRating);
        productRepository.save(product);

        // 6. Return response
        return ReviewMapper.toResponseDto(savedReview);
    }
}

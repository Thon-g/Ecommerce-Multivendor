package com.abs.app.infrastructure.mapper;

import com.abs.app.application.publicapi.review.dto.ReviewResponseDto;
import com.abs.app.domain.entity.Review;

public class ReviewMapper {
    public static ReviewResponseDto toResponseDto(Review review) {
        if (review == null) {
            return null;
        }

        ReviewResponseDto dto = new ReviewResponseDto();
        dto.setId(review.getId());
        dto.setReviewText(review.getReviewText());
        dto.setRating(review.getRating());
        dto.setProductImages(review.getProductImages());
        dto.setProductId(review.getProduct() != null ? review.getProduct().getId() : null);
        dto.setUserId(review.getUser() != null ? review.getUser().getUserId() : null);
        dto.setUserName(review.getUser() != null ? review.getUser().getFirstName() + " " + review.getUser().getLastName() : null);
        dto.setUserImage(review.getUser() != null ? review.getUser().getPicture() : null);
        dto.setCreateAt(review.getCreateAt());
        
        return dto;
    }
}

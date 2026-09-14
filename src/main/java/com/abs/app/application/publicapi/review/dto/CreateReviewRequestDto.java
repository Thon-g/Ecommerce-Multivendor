package com.abs.app.application.publicapi.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

import com.abs.app.common.constant.ReviewConstant;

@Data
public class CreateReviewRequestDto {
    @NotBlank(message = ReviewConstant.PRODUCT_ID_REQUIRED)
    private String productId;

    @NotBlank(message = ReviewConstant.REVIEW_TEXT_REQUIRED)
    private String reviewText;

    @NotNull(message = ReviewConstant.RATING_REQUIRED)
    @Min(value = 1, message = ReviewConstant.RATING_MIN)
    @Max(value = 5, message = ReviewConstant.RATING_MAX)
    private Double rating;

    private List<String> productImages;
}

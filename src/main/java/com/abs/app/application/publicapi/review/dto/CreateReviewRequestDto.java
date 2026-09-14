package com.abs.app.application.publicapi.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateReviewRequestDto {
    @NotBlank(message = "Product ID is required")
    private String productId;

    @NotBlank(message = "Review text is required")
    private String reviewText;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Double rating;

    private List<String> productImages;
}

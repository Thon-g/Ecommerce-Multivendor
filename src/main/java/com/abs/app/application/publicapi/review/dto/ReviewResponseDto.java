package com.abs.app.application.publicapi.review.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReviewResponseDto {
    private Long id;
    private String reviewText;
    private Double rating;
    private List<String> productImages;
    private String productId;
    private String userId;
    private String userName; // to display who wrote the review
    private String userImage;
    private LocalDateTime createAt;
}

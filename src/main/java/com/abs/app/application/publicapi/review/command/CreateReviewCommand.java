package com.abs.app.application.publicapi.review.command;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CreateReviewCommand {
    private String userId;
    private String productId;
    private String reviewText;
    private Double rating;
    private List<String> productImages;
}

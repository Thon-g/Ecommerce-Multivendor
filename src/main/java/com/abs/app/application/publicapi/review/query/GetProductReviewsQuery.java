package com.abs.app.application.publicapi.review.query;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetProductReviewsQuery {
    private String productId;
    private int page;
    private int limit;
}

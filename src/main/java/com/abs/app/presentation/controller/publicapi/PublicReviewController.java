package com.abs.app.presentation.controller.publicapi;

import com.abs.app.application.publicapi.review.dto.ReviewResponseDto;
import com.abs.app.application.publicapi.review.query.GetProductReviewsQuery;
import com.abs.app.application.publicapi.review.query.GetProductReviewsQueryHandler;
import com.abs.app.common.response.ApiResponse;
import com.abs.app.common.response.PageResponse;
import com.abs.app.common.constant.ReviewConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/reviews")
@RequiredArgsConstructor
public class PublicReviewController {

    private final GetProductReviewsQueryHandler getProductReviewsQueryHandler;

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponseDto>>> getProductReviews(
            @PathVariable String productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        
        GetProductReviewsQuery query = new GetProductReviewsQuery(productId, page, limit);
        PageResponse<ReviewResponseDto> response = getProductReviewsQueryHandler.handle(query);
        
        return ResponseEntity.ok(new ApiResponse<>(true, ReviewConstant.REVIEWS_RETRIEVED_SUCCESS, response));
    }
}

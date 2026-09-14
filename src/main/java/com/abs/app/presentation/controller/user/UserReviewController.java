package com.abs.app.presentation.controller.user;

import com.abs.app.application.publicapi.review.command.CreateReviewCommand;
import com.abs.app.application.publicapi.review.command.CreateReviewCommandHandler;
import com.abs.app.application.publicapi.review.dto.CreateReviewRequestDto;
import com.abs.app.application.publicapi.review.dto.ReviewResponseDto;
import com.abs.app.common.response.ApiResponse;
import com.abs.app.infrastructure.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/reviews")
@RequiredArgsConstructor
public class UserReviewController {

    private final CreateReviewCommandHandler createReviewCommandHandler;

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponseDto>> createReview(
            @Valid @RequestBody CreateReviewRequestDto request) {
        String userId = SecurityUtils.getCurrentUserId();
        
        CreateReviewCommand command = new CreateReviewCommand(
                userId,
                request.getProductId(),
                request.getReviewText(),
                request.getRating(),
                request.getProductImages()
        );
        
        ReviewResponseDto response = createReviewCommandHandler.handle(command);
        return new ResponseEntity<>(new ApiResponse<>(true, "Review submitted successfully", response), HttpStatus.CREATED);
    }
}

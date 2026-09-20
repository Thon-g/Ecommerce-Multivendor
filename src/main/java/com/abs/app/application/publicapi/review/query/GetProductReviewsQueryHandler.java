package com.abs.app.application.publicapi.review.query;

import com.abs.app.application.publicapi.review.dto.ReviewResponseDto;
import com.abs.app.common.response.PageResponse;
import com.abs.app.domain.entity.Review;
import com.abs.app.domain.repository.ReviewRepository;
import com.abs.app.infrastructure.mapper.ReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetProductReviewsQueryHandler {

    private final ReviewRepository reviewRepository;

    public PageResponse<ReviewResponseDto> handle(GetProductReviewsQuery query) {
        Pageable pageable = PageRequest.of(query.getPage(), query.getLimit(), Sort.by(Sort.Direction.DESC, "createAt"));
        Page<Review> reviewPage = reviewRepository.findByProductId(query.getProductId(), pageable);

        List<ReviewResponseDto> dtoList = reviewPage.getContent().stream()
                .map(ReviewMapper::toResponseDto)
                .collect(Collectors.toList());

        return new PageResponse<>(
                dtoList,
                (int) reviewPage.getTotalElements(),
                query.getPage(),
                query.getLimit()
        );
    }
}

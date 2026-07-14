package com.spring.its_here.domain.review.controller;


import com.spring.its_here.domain.review.dto.request.ReviewCreateRequestDto;
import com.spring.its_here.domain.review.dto.request.ReviewUpdateRequestDto;
import com.spring.its_here.domain.review.dto.response.ReviewCreateResponseDto;
import com.spring.its_here.domain.review.dto.response.ReviewUpdateResponseDto;
import com.spring.its_here.domain.review.service.ReviewService;
import com.spring.its_here.global.response.ApiResponse;
import com.spring.its_here.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewCreateResponseDto>> createReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ReviewCreateRequestDto reviewCreateRequestDto
    ) {
        ReviewCreateResponseDto reviewCreateResponseDto = reviewService.createReview(
                reviewCreateRequestDto,
                userDetails
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("리뷰 작성 성공", reviewCreateResponseDto));
    }

    // 리뷰 작성 후 24시간 이내 수정 가능하도록 했는가?
    // 이미 삭제된 리뷰 수정 불가능하게 했는가?
    @PutMapping("{reviewId}")
    public ResponseEntity<ApiResponse<ReviewUpdateResponseDto>> updateReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("reviewId") UUID reviewId,
            @Valid @RequestBody ReviewUpdateRequestDto reviewUpdateRequestDto
    ) {
        ReviewUpdateResponseDto reviewUpdateResponseDto = reviewService.updateReview(
                userDetails,
                reviewId,
                reviewUpdateRequestDto
        );

        return ResponseEntity.ok(ApiResponse.success("리뷰 수정 성공", reviewUpdateResponseDto));
    }
}

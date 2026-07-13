package com.spring.its_here.domain.review.controller;


import com.spring.its_here.domain.review.dto.request.ReviewCreateRequestDto;
import com.spring.its_here.domain.review.dto.request.ReviewGetAllRequestDto;
import com.spring.its_here.domain.review.dto.response.ReviewCreateResponseDto;
import com.spring.its_here.domain.review.dto.response.ReviewGetAllResponseDto;
import com.spring.its_here.domain.review.dto.response.ReviewGetOneResponseDto;
import com.spring.its_here.domain.review.service.ReviewService;
import com.spring.its_here.global.response.ApiResponse;
import com.spring.its_here.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewGetOneResponseDto>> getOneReview(
            @PathVariable("reviewId") UUID reviewId
    ) {
        ReviewGetOneResponseDto reviewGetOneResponseDto = reviewService.getOneReview(reviewId);

        return ResponseEntity.ok(ApiResponse.success("리뷰 단건 조회 성공", reviewGetOneResponseDto));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ReviewGetAllResponseDto>> getAllReview(
            @ModelAttribute ReviewGetAllRequestDto reviewGetAllRequestDto,
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        ReviewGetAllResponseDto reviewGetAllResponseDto = reviewService.getAllReview(
                reviewGetAllRequestDto,
                pageable
        );

        return ResponseEntity.ok(ApiResponse.success(
                "리뷰 전체 조회 성공",
                reviewGetAllResponseDto
        ));
    }
}

package com.spring.its_here.domain.review.controller;


import com.spring.its_here.domain.review.dto.request.ReviewCreateRequestDto;
import com.spring.its_here.domain.review.dto.request.ReviewGetAllRequestDto;
import com.spring.its_here.domain.review.dto.request.ReviewUpdateRequestDto;
import com.spring.its_here.domain.review.dto.response.ReviewCreateResponseDto;
import com.spring.its_here.domain.review.dto.response.ReviewGetAllResponseDto;
import com.spring.its_here.domain.review.dto.response.ReviewGetOneResponseDto;
import com.spring.its_here.domain.review.dto.response.ReviewUpdateResponseDto;
import com.spring.its_here.domain.review.service.ReviewService;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import com.spring.its_here.global.response.ApiResponse;
import com.spring.its_here.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
public class ReviewController implements ReviewApi {
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
        validatorSortBy(pageable);
        Pageable normalizeSize = normalizeSize(pageable);

        ReviewGetAllResponseDto reviewGetAllResponseDto = reviewService.getAllReview(
                reviewGetAllRequestDto,
                normalizeSize
        );

        return ResponseEntity.ok(ApiResponse.success(
                "리뷰 전체 조회 성공",
                reviewGetAllResponseDto
        ));
    }

    private Pageable normalizeSize(Pageable pageable) {
        int size = pageable.getPageSize();

        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }
        return PageRequest.of(
                pageable.getPageNumber(),
                size,
                pageable.getSort()
        );
    }

    private void validatorSortBy(Pageable pageable) {
        for (Sort.Order order : pageable.getSort()) {
            if (!order.getProperty().equals("createdAt")) {
                throw new ItsHereException(ErrorCode.REVIEW_INVALID_SORT_BY);
            }
        }
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

package com.spring.its_here.domain.review.controller;


import com.spring.its_here.domain.review.dto.request.ReviewCreateRequestDto;
import com.spring.its_here.domain.review.dto.response.ReviewCreateResponseDto;
import com.spring.its_here.domain.review.service.ReviewService;
import com.spring.its_here.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewCreateResponseDto>> create(
            @Valid @RequestBody ReviewCreateRequestDto reviewCreateRequestDto
    ) {
        ReviewCreateResponseDto reviewCreateResponseDto = reviewService.create(reviewCreateRequestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("리뷰 작성 성공", reviewCreateResponseDto));
    }
}

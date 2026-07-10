package com.spring.its_here.domain.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.its_here.domain.review.dto.request.ReviewCreateRequestDto;
import com.spring.its_here.domain.review.dto.response.ReviewCreateResponseDto;
import com.spring.its_here.domain.review.service.ReviewService;
import com.spring.its_here.global.config.SecurityConfig;
import com.spring.its_here.global.security.CustomUserDetails;
import com.spring.its_here.global.security.CustomUserDetailsService;
import com.spring.its_here.global.security.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
@Import(SecurityConfig.class)
class ReviewControllerTest {
    UUID reviewId = UUID.randomUUID();
    UUID orderId = UUID.randomUUID();
    UUID storeId = UUID.randomUUID();
    Long userId = 1L;

    @MockitoBean
    ReviewService reviewService;

    @MockitoBean
    JwtProvider jwtProvider;

    @MockitoBean
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Nested
    @DisplayName("리뷰 생성")
    class ReviewCreate {
        @Test
        @DisplayName("성공")
        void create() throws Exception {
            CustomUserDetails customUserDetails = mock(CustomUserDetails.class);

            ReviewCreateRequestDto reviewCreateRequestDto = new ReviewCreateRequestDto(
                    orderId,
                    3.0,
                    "content"
            );
            ReviewCreateResponseDto reviewCreateResponseDto = new ReviewCreateResponseDto(
                    reviewId,
                    orderId,
                    storeId,
                    userId
            );
            given(reviewService.createReview(any(ReviewCreateRequestDto.class), eq(customUserDetails))).willReturn(reviewCreateResponseDto);

            mockMvc.perform(
                            post("/api/reviews")
                                    .with(user(customUserDetails))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(reviewCreateRequestDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").value("리뷰 작성 성공"))
                    .andExpect(jsonPath("$.code").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.reviewId").value(reviewId.toString()))
                    .andExpect(jsonPath("$.data.orderId").value(orderId.toString()))
                    .andExpect(jsonPath("$.data.storeId").value(storeId.toString()))
                    .andExpect(jsonPath("$.data.userId").value(userId));
        }

        @ParameterizedTest
        @ValueSource(doubles = {
                Double.NaN,
                0.5,
                0.0,
                -1.0,
                5.1,
                100.0
        })
        @DisplayName("유효하지 않은 평점")
        void invalid_rating(double rating) throws Exception {
            ReviewCreateRequestDto reviewCreateRequestDto = new ReviewCreateRequestDto(
                    orderId,
                    rating,
                    "content"
            );
            mockMvc.perform(
                            post("/api/reviews")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(reviewCreateRequestDto))
                    )
                    .andExpect(status().isBadRequest());

            verify(reviewService, never()).createReview(any(ReviewCreateRequestDto.class), any(CustomUserDetails.class));
        }

        @Test
        @DisplayName("리뷰 내용 255자 초과 예외")
        void content_too_long() throws Exception {
            ReviewCreateRequestDto request = new ReviewCreateRequestDto(
                    orderId,
                    3.0,
                    "a".repeat(256)
            );
            mockMvc.perform(
                            post("/api/reviews")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    )
                    .andExpect(status().isBadRequest());

            verify(reviewService, never()).createReview(any(ReviewCreateRequestDto.class), any(CustomUserDetails.class));
        }
    }
}
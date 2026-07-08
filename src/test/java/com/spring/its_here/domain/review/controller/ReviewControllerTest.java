package com.spring.its_here.domain.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.its_here.domain.review.dto.request.ReviewCreateRequestDto;
import com.spring.its_here.domain.review.dto.response.ReviewCreateResponseDto;
import com.spring.its_here.domain.review.service.ReviewService;
import com.spring.its_here.global.advice.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {
    UUID reviewId = UUID.randomUUID();
    UUID orderId = UUID.randomUUID();
    UUID storeId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    @InjectMocks
    ReviewController reviewController;

    @Mock
    ReviewService reviewService;

    MockMvc mockMvc;
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(reviewController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("리뷰 생성")
    class ReviewCreate {
        @Test
        @DisplayName("성공")
        void create() throws Exception {
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
            given(reviewService.create(any(ReviewCreateRequestDto.class))).willReturn(reviewCreateResponseDto);

            mockMvc.perform(
                            post("/api/reviews")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(reviewCreateRequestDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").value("리뷰 작성 성공"))
                    .andExpect(jsonPath("$.code").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.id").value(reviewId.toString()))
                    .andExpect(jsonPath("$.data.orderId").value(orderId.toString()))
                    .andExpect(jsonPath("$.data.storeId").value(storeId.toString()))
                    .andExpect(jsonPath("$.data.userId").value(userId.toString()));
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
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("INVALID_RATING"));

            verify(reviewService, never()).create(any(ReviewCreateRequestDto.class));
        }

        @Test
        @DisplayName("리뷰 내용 255자 초과")
        void content_too_long() throws Exception {
            ReviewCreateRequestDto reviewCreateRequestDto = new ReviewCreateRequestDto(
                    orderId,
                    3.0,
                    "a".repeat(256)
            );

            mockMvc.perform(
                            post("/api/reviews")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(reviewCreateRequestDto))
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("CONTENT_TOO_LONG"));

            verify(reviewService, never()).create(any(ReviewCreateRequestDto.class));
        }
    }
}
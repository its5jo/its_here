package com.spring.its_here.domain.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.its_here.domain.review.dto.request.ReviewCreateRequestDto;
import com.spring.its_here.domain.review.dto.request.ReviewGetAllRequestDto;
import com.spring.its_here.domain.review.dto.response.ReviewCreateResponseDto;
import com.spring.its_here.domain.review.dto.response.ReviewGetAllItemsResponseDto;
import com.spring.its_here.domain.review.dto.response.ReviewGetAllResponseDto;
import com.spring.its_here.domain.review.dto.response.ReviewGetOneResponseDto;
import com.spring.its_here.domain.review.service.ReviewService;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import com.spring.its_here.global.config.SecurityConfig;
import com.spring.its_here.global.response.OffsetPageInfo;
import com.spring.its_here.global.security.CustomUserDetails;
import com.spring.its_here.global.security.CustomUserDetailsService;
import com.spring.its_here.global.security.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
                0.5,
                0.0,
                -1.0,
                1.5,
                2.5,
                4.5,
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
                                    .content(objectMapper.writeValueAsString(reviewCreateRequestDto)))
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

    /*
    * {
	"message": "리뷰 단건 조회 성공",
	"code": "SUCCESS",
	"data": {
		"reviewId" : "UUID",
		"orderId" : "UUID",
		"storeId" : "UUID",
		"userId" : 1L,
		"rating" : "UUID",
		"content" : "너무 맛있어요",
		"createdAt" : "",
		"updatedAt" : ""
	}
}
    *
    * */
    @Nested
    @DisplayName("조회")
    class getReview {
        @Test
        @DisplayName("단건조회 성공")
        void getOneReview_success() throws Exception {
            ReviewGetOneResponseDto reviewGetOneResponseDto = new ReviewGetOneResponseDto(
                    reviewId,
                    orderId,
                    storeId,
                    userId,
                    3.0,
                    "content",
                    Instant.parse("2026-07-07T06:00:00Z"),
                    Instant.parse("2026-07-07T06:00:00Z")
            );
            given(reviewService.getOneReview(reviewId)).willReturn(reviewGetOneResponseDto);
            mockMvc.perform(get("/api/reviews/{reviewId}", reviewId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("리뷰 단건 조회 성공"))
                    .andExpect(jsonPath("$.code").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.reviewId").value(reviewId.toString()))
                    .andExpect(jsonPath("$.data.orderId").value(orderId.toString()))
                    .andExpect(jsonPath("$.data.storeId").value(storeId.toString()))
                    .andExpect(jsonPath("$.data.userId").value(userId.toString()))
                    .andExpect(jsonPath("$.data.rating").value(3.0))
                    .andExpect(jsonPath("$.data.content").value("content"));

            verify(reviewService).getOneReview(reviewId);
        }

        @Test
        @DisplayName("존재하지 않는 리뷰면 예외")
        void getOneReview_not_found() throws Exception {
            given(reviewService.getOneReview(reviewId)).willThrow(new ItsHereException(ErrorCode.REVIEW_NOT_FOUND));

            mockMvc.perform(get("/api/reviews/{reviewId}", reviewId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(ErrorCode.REVIEW_NOT_FOUND.getCode()))
                    .andExpect(jsonPath("$.message").value("리뷰가 존재하지 않습니다."));
            verify(reviewService).getOneReview(reviewId);
        }

        @Test
        @DisplayName("전체조회 성공")
        void getAllReview_success() throws Exception {
            ReviewGetAllItemsResponseDto reviewGetAllItemsResponseDto = new ReviewGetAllItemsResponseDto(
                    reviewId,
                    userId,
                    3.0,
                    "content",
                    Instant.parse("2026-07-07T06:00:00Z")
            );
            OffsetPageInfo pageInfo = new OffsetPageInfo(
                    "OFFSET",
                    false,
                    1L,
                    "createdAt",
                    "DESC"
            );
            ReviewGetAllResponseDto reviewGetAllResponseDto = new ReviewGetAllResponseDto(
                    List.of(reviewGetAllItemsResponseDto),
                    pageInfo
            );

            given(reviewService.getAllReview(
                    any(ReviewGetAllRequestDto.class),
                    any(Pageable.class)
            )).willReturn(reviewGetAllResponseDto);

            mockMvc.perform(get("/api/reviews"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("리뷰 전체 조회 성공"))
                    .andExpect(jsonPath("$.code").value("SUCCESS"))
            ;

            ArgumentCaptor<ReviewGetAllRequestDto> requestCaptor = ArgumentCaptor.forClass(ReviewGetAllRequestDto.class);
            ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

            verify(reviewService).getAllReview(
                    requestCaptor.capture(),
                    pageableCaptor.capture()
            );
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 20, 40})
        @DisplayName("조회개수가 10, 30, 50이 아니면 10건으로 조회")
        void getAllReview_invalid_size(int size) throws Exception {
            OffsetPageInfo pageInfo = new OffsetPageInfo(
                    "OFFSET",
                    false,
                    0L,
                    "createdAt",
                    "DESC"
            );
            ReviewGetAllResponseDto reviewGetAllResponseDto = new ReviewGetAllResponseDto(
                    List.of(),
                    pageInfo
            );

            given(reviewService.getAllReview(
                    any(ReviewGetAllRequestDto.class),
                    any(Pageable.class)
            )).willReturn(reviewGetAllResponseDto);

            mockMvc.perform(get("/api/reviews")
                            .param("size", String.valueOf(size)))
                    .andExpect(status().isOk());

            verify(reviewService).getAllReview(
                    any(ReviewGetAllRequestDto.class),
                    any(Pageable.class)
            );
        }

        @Test
        @DisplayName("정렬 기준이 createdAt이 아니면 예외")
        void getAllReview_invalid_sort() throws Exception {
            mockMvc.perform(get("/api/reviews")
                            .param("size", "10")
                            .param("sort", "updatedAt, desc"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(ErrorCode.REVIEW_INVALID_SORT_BY.getCode()));

            verify(reviewService, never()).getAllReview(any(), any());
        }
    }
}

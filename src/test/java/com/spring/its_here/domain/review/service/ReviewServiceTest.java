package com.spring.its_here.domain.review.service;

import com.spring.its_here.domain.review.dto.request.ReviewCreateRequestDto;
import com.spring.its_here.domain.review.dto.response.ReviewCreateResponseDto;
import com.spring.its_here.domain.review.entity.Review;
import com.spring.its_here.domain.review.repository.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {
    UUID reviewId = UUID.randomUUID();
    UUID orderId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    UUID storeId = UUID.randomUUID();

    @InjectMocks
    ReviewService reviewService;

    @Mock
    ReviewRepository reviewRepository;

//    @Mock
//    OrderRepository orderRepository;

    @Nested
    @DisplayName("리뷰 생성")
    class ReviewCreateTest{
        @Test
        @DisplayName("성공")
        void create(){
            Review reviewSave = Review.create(3, "content");
            ReflectionTestUtils.setField(reviewSave, "id", reviewId);
            ReflectionTestUtils.setField(reviewSave, "orderId", orderId);
            ReflectionTestUtils.setField(reviewSave, "storeId", storeId);
            ReflectionTestUtils.setField(reviewSave, "userId", userId);

            given(reviewRepository.save(any(Review.class))).willReturn(reviewSave);
            ReviewCreateRequestDto reviewCreate = new ReviewCreateRequestDto(
                    orderId,
                    3,
                    "content"
            );

            ReviewCreateResponseDto response = reviewService.create(reviewCreate);

            assertThat(response).isNotNull();
            assertThat(response.orderId()).isEqualTo(orderId);
            assertThat(response.userId()).isEqualTo(userId);
            assertThat(response.storeId()).isEqualTo(storeId);

            verify(reviewRepository).save(any(Review.class));
        }

        @Test
        @DisplayName("주문 완료 상태 아닌 경우 예외")
        void order_not_completed(){

        }

        @Test
        @DisplayName("존재하지 않는 주문인 경우 예외")
        void order_not_found(){

        }

        @Test
        @DisplayName("이미 존재하는 리뷰인 경우 예외")
        void review_already_exists(){

        }
    }
}
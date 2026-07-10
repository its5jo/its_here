package com.spring.its_here.domain.review.service;

import com.spring.its_here.domain.order.entity.Order;
import com.spring.its_here.domain.order.enums.OrderStatus;
import com.spring.its_here.domain.order.repository.OrderRepository;
import com.spring.its_here.domain.review.dto.request.ReviewCreateRequestDto;
import com.spring.its_here.domain.review.dto.response.ReviewCreateResponseDto;
import com.spring.its_here.domain.review.entity.Review;
import com.spring.its_here.domain.review.repository.ReviewRepository;
import com.spring.its_here.domain.user.entity.UserEntity;
import com.spring.its_here.domain.user.enums.UserRole;
import com.spring.its_here.domain.user.repository.UserRepository;
import com.spring.its_here.global.advice.ItsHereException;
import com.spring.its_here.global.security.AuthenticationFacade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {
    UUID reviewId = UUID.randomUUID();
    UUID orderId = UUID.randomUUID();
    Long userId = 1L;
    UUID storeId = UUID.randomUUID();

    @InjectMocks
    ReviewService reviewService;

    @Mock
    ReviewRepository reviewRepository;

    @Mock
    OrderRepository orderRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    AuthenticationFacade authenticationFacade;

    private UserEntity createUser(Long id) {
        UserEntity user = UserEntity.create(
                "kim123",
                "password",
                "르탄이",
                UserRole.CUSTOMER
        );

        ReflectionTestUtils.setField(user, "id", id);

        return user;
    }

    private Order createOrder(Long ownerId, OrderStatus status) {
        Order order = Order.create(
                storeId,
                ownerId,
                "서울특별시 강남구",
                "문 앞에 놓아주세요",
                20000
        );

        ReflectionTestUtils.setField(order, "id", orderId);
        ReflectionTestUtils.setField(order, "status", status);

        return order;
    }

    private ReviewCreateRequestDto createRequest() {
        return new ReviewCreateRequestDto(
                orderId,
                3.0,
                "content"
        );
    }

    @Nested
    @DisplayName("리뷰 생성")
    class ReviewCreateTest {
        @Test
        @DisplayName("성공")
        void create() {
            Order order = mock(Order.class);
            UserEntity user = UserEntity.create(
                    "username",
                    "password",
                    "nickname",
                    UserRole.CUSTOMER
            );
            ReflectionTestUtils.setField(user, "id", userId);

            given(authenticationFacade.getCurrentUserId()).willReturn(userId);
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            given(order.getId()).willReturn(orderId);
            given(order.getUserId()).willReturn(userId);
            given(order.getStoreId()).willReturn(storeId);
            given(order.getStatus()).willReturn(OrderStatus.COMPLETED);

            Review reviewSave = Review.create(
                    3.0,
                    "content",
                    order,
                    user
            );

            ReflectionTestUtils.setField(reviewSave, "id", reviewId);

            given(reviewRepository.save(any(Review.class))).willReturn(reviewSave);

            ReviewCreateRequestDto request = new ReviewCreateRequestDto(
                    orderId,
                    3.0,
                    "content"
            );

            ReviewCreateResponseDto response = reviewService.create(request);

            assertThat(response).isNotNull();
            assertThat(response.reviewId()).isEqualTo(reviewId);
            assertThat(response.orderId()).isEqualTo(orderId);
            assertThat(response.storeId()).isEqualTo(storeId);
            assertThat(response.userId()).isEqualTo(userId);

            verify(reviewRepository).save(any(Review.class));
        }

        @Test
        @DisplayName("주문 완료 상태 아닌 경우 예외")
        void order_not_completed() {
            UserEntity user = createUser(userId);
            Order order = createOrder(userId, OrderStatus.REQUESTED);

            ReviewCreateRequestDto reviewCreateRequestDto = createRequest();

            given(authenticationFacade.getCurrentUserId()).willReturn(userId);
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            assertThatThrownBy(() -> reviewService.create(reviewCreateRequestDto))
                    .isInstanceOf(ItsHereException.class);

            verify(reviewRepository, never()).existsByOrder_Id(any());
            verify(reviewRepository, never()).save(any(Review.class));
        }

        @Test
        @DisplayName("존재하지 않는 주문인 경우 예외")
        void order_not_found() {
            UserEntity user = createUser(userId);
            ReviewCreateRequestDto reviewCreateRequestDto = createRequest();

            given(authenticationFacade.getCurrentUserId()).willReturn(userId);
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(orderRepository.findById(orderId)).willReturn(Optional.empty());

            assertThatThrownBy(() -> reviewService.create(reviewCreateRequestDto))
                    .isInstanceOf(ItsHereException.class);

            verify(reviewRepository, never()).existsByOrder_Id(any());
            verify(reviewRepository, never()).save(any(Review.class));
        }

        @Test
        @DisplayName("본인의 주문이 아닌 경우")
        void review_forbidden() {
            Long anotherUserId = 2L;

            UserEntity user = createUser(userId);
            Order order = createOrder(
                    anotherUserId,
                    OrderStatus.COMPLETED
            );

            ReviewCreateRequestDto reviewCreateRequestDto = createRequest();

            given(authenticationFacade.getCurrentUserId()).willReturn(userId);
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            assertThatThrownBy(() -> reviewService.create(reviewCreateRequestDto))
                    .isInstanceOf(ItsHereException.class);

            verify(reviewRepository, never()).existsByOrder_Id(any());

            verify(reviewRepository, never()).save(any(Review.class));
        }

        @Test
        @DisplayName("이미 존재하는 리뷰인 경우 예외")
        void review_already_exists() {
            UserEntity user = createUser(userId);
            Order order = createOrder(userId, OrderStatus.COMPLETED);

            ReviewCreateRequestDto reviewCreateRequestDto = createRequest();
            given(authenticationFacade.getCurrentUserId()).willReturn(userId);
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            given(reviewRepository.existsByOrder_Id(orderId)).willReturn(true);

            assertThatThrownBy(() -> reviewService.create(reviewCreateRequestDto))
                    .isInstanceOf(ItsHereException.class);

            verify(reviewRepository, never()).save(any(Review.class));
        }
    }
}
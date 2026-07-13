package com.spring.its_here.domain.review.service;

import com.spring.its_here.domain.area.entity.Area;
import com.spring.its_here.domain.category.entity.Category;
import com.spring.its_here.domain.order.entity.Order;
import com.spring.its_here.domain.order.enums.OrderStatus;
import com.spring.its_here.domain.order.repository.OrderRepository;
import com.spring.its_here.domain.review.dto.request.ReviewCreateRequestDto;
import com.spring.its_here.domain.review.dto.response.ReviewCreateResponseDto;
import com.spring.its_here.domain.review.dto.response.ReviewGetOneResponseDto;
import com.spring.its_here.domain.review.entity.Review;
import com.spring.its_here.domain.review.repository.ReviewRepository;
import com.spring.its_here.domain.store.entity.Store;
import com.spring.its_here.domain.store.repository.StoreRepository;
import com.spring.its_here.domain.user.entity.UserEntity;
import com.spring.its_here.domain.user.enums.UserRole;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import com.spring.its_here.global.security.CustomUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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
    StoreRepository storeRepository;

    @Mock
    OrderRepository orderRepository;

    private UserEntity createTestUser(Long id) {
        UserEntity user = UserEntity.create(
                "kim123",
                "password",
                "르탄이",
                UserRole.CUSTOMER
        );

        ReflectionTestUtils.setField(user, "id", id);

        return user;
    }

    private Order createTestOrder(
            Long ownerId,
            OrderStatus status
    ) {
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

    private Store createTestStore(UserEntity user) {
        Area area = Area.create(
                "city",
                "district",
                "town"
        );
        Category category = Category.createCategory(
                "한식이젤루좋아",
                false
        );
        Store store = Store.createStore(
                "name",
                "address",
                user,
                category,
                area,
                true,
                null,
                null
        );
        ReflectionTestUtils.setField(store, "id", storeId);

        return store;
    }

    private CustomUserDetails createUserDetails(UserEntity user) {
        CustomUserDetails customUserDetails = mock(CustomUserDetails.class);
        given(customUserDetails.getUserEntity()).willReturn(user);

        return customUserDetails;
    }

    private ReviewCreateRequestDto createTestRequest() {
        return new ReviewCreateRequestDto(
                orderId,
                3.0,
                "content"
        );
    }

    @Nested
    @DisplayName("리뷰 생성")
    class CreateReview {
        @Test
        @DisplayName("성공")
        void success() {
            UserEntity user = createTestUser(userId);
            CustomUserDetails customUserDetails = createUserDetails(user);
            Store store = createTestStore(user);
            Order order = createTestOrder(
                    userId,
                    OrderStatus.COMPLETED
            );
            ReviewCreateRequestDto request = createTestRequest();

            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            given(reviewRepository.existsByOrderId(orderId)).willReturn(false);
            given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
            Review savedReview = Review.savedReview(
                    3.0,
                    "content",
                    user,
                    store,
                    order
            );

            ReflectionTestUtils.setField(savedReview, "id", reviewId);
            given(reviewRepository.save(any(Review.class)))
                    .willReturn(savedReview);

            ReviewCreateResponseDto response = reviewService.createReview(request, customUserDetails);

            assertThat(response).isNotNull();
            assertThat(response.reviewId()).isEqualTo(reviewId);
            assertThat(response.orderId()).isEqualTo(orderId);
            assertThat(response.storeId()).isEqualTo(storeId);
            assertThat(response.userId()).isEqualTo(userId);
            assertThat(store.getReviewTotalRating()).isEqualTo(3.0);
            assertThat(store.getReviewTotalCount()).isEqualTo(1L);

            verify(customUserDetails).getUserEntity();
            verify(orderRepository).findById(orderId);
            verify(reviewRepository).existsByOrderId(orderId);
            verify(storeRepository).findById(storeId);
            verify(reviewRepository).save(any(Review.class));
        }

        @Test
        @DisplayName("주문 완료 상태 아닌 경우 예외")
        void order_not_completed() {
            UserEntity user = createTestUser(userId);
            CustomUserDetails userDetails = createUserDetails(user);

            Order order = createTestOrder(
                    userId,
                    OrderStatus.REQUESTED
            );

            ReviewCreateRequestDto request = createTestRequest();

            given(orderRepository.findById(orderId))
                    .willReturn(Optional.of(order));

            assertThatThrownBy(
                    () -> reviewService.createReview(request, userDetails))
                    .isInstanceOfSatisfying(
                            ItsHereException.class,
                            exception -> assertThat(exception.getErrorCode())
                                    .isEqualTo(ErrorCode.REVIEW_ORDER_NOT_COMPLETED)
                    );

            verify(reviewRepository, never()).existsByOrderId(any());
            verify(storeRepository, never()).findById(any());
            verify(reviewRepository, never()).save(any());
        }

        @Test
        @DisplayName("주문이 존재하지 않으면 예외")
        void order_not_found() {
            UserEntity user = createTestUser(userId);
            CustomUserDetails userDetails = createUserDetails(user);
            ReviewCreateRequestDto request = createTestRequest();

            given(orderRepository.findById(orderId)).willReturn(Optional.empty());

            assertThatThrownBy(
                    () -> reviewService.createReview(request, userDetails))
                    .isInstanceOfSatisfying(
                            ItsHereException.class,
                            exception -> assertThat(exception.getErrorCode())
                                    .isEqualTo(ErrorCode.ORDER_NOT_FOUND)
                    );

            verify(reviewRepository, never()).existsByOrderId(any());
            verify(storeRepository, never()).findById(any());
            verify(reviewRepository, never()).save(any());
        }

        @Test
        @DisplayName("본인의 주문이 아니면 예외")
        void review_forbidden() {
            UserEntity user = createTestUser(userId);
            CustomUserDetails userDetails = createUserDetails(user);

            Order order = createTestOrder(
                    2L,
                    OrderStatus.COMPLETED
            );

            ReviewCreateRequestDto request = createTestRequest();

            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            assertThatThrownBy(
                    () -> reviewService.createReview(request, userDetails))
                    .isInstanceOfSatisfying(
                            ItsHereException.class,
                            exception -> assertThat(exception.getErrorCode())
                                    .isEqualTo(ErrorCode.REVIEW_FORBIDDEN)
                    );

            verify(reviewRepository, never()).existsByOrderId(any());
            verify(storeRepository, never()).findById(any());
            verify(reviewRepository, never()).save(any());
        }

        @Test
        @DisplayName("주문에 이미 리뷰가 존재하면 예외")
        void review_already_exists() {
            UserEntity user = createTestUser(userId);
            CustomUserDetails userDetails = createUserDetails(user);

            Order order = createTestOrder(
                    userId,
                    OrderStatus.COMPLETED
            );

            ReviewCreateRequestDto request = createTestRequest();

            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            given(reviewRepository.existsByOrderId(orderId)).willReturn(true);

            assertThatThrownBy(
                    () -> reviewService.createReview(request, userDetails))
                    .isInstanceOfSatisfying(
                            ItsHereException.class,
                            exception -> assertThat(exception.getErrorCode())
                                    .isEqualTo(ErrorCode.REVIEW_ALREADY_EXISTS)
                    );

            verify(storeRepository, never()).findById(any());
            verify(reviewRepository, never()).save(any());
        }

        @Test
        @DisplayName("가게가 존재하지 않으면 예외")
        void store_not_found() {
            UserEntity user = createTestUser(userId);
            CustomUserDetails userDetails = createUserDetails(user);

            Order order = createTestOrder(
                    userId,
                    OrderStatus.COMPLETED
            );

            ReviewCreateRequestDto request = createTestRequest();

            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            given(reviewRepository.existsByOrderId(orderId)).willReturn(false);
            given(storeRepository.findById(storeId)).willReturn(Optional.empty());

            assertThatThrownBy(
                    () -> reviewService.createReview(request, userDetails))
                    .isInstanceOfSatisfying(
                            ItsHereException.class,
                            exception -> assertThat(exception.getErrorCode())
                                    .isEqualTo(ErrorCode.STORE_NOT_FOUND)
                    );

            verify(reviewRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("조회")
    class getReview {
        @Test
        @DisplayName("단건조회 성공")
        void getOneReview_success() {
            UserEntity user = createTestUser(userId);
            Store store = createTestStore(user);
            Order order = createTestOrder(
                    userId,
                    OrderStatus.COMPLETED
            );

            Review reviewSave = Review.savedReview(
                    3.0,
                    "content",
                    user,
                    store,
                    order
            );
            ReflectionTestUtils.setField(reviewSave, "id", reviewId);

            given(reviewRepository.findByIdAndDeletedAtIsNull(reviewId)).willReturn(Optional.of(reviewSave));

            ReviewGetOneResponseDto reviewGetOneResponseDto = reviewService.getOneReview(reviewId);

            assertThat(reviewGetOneResponseDto).isNotNull();
            assertThat(reviewGetOneResponseDto.reviewId()).isEqualTo(reviewId);

            verify(reviewRepository).findByIdAndDeletedAtIsNull(reviewId);
        }

        @Test
        @DisplayName("단건 조회 시 존재하지 않는 리뷰면 예외")
        void getOneReview_not_found() {
            ItsHereException itsHereException = assertThrows(
                    ItsHereException.class,
                    () -> reviewService.getOneReview(reviewId)
            );

            assertThat(itsHereException.getErrorCode()).isEqualTo(ErrorCode.REVIEW_NOT_FOUND);

            verify(reviewRepository).findByIdAndDeletedAtIsNull(reviewId);
        }
        
        @Test
        @DisplayName("전체조회 성공")
        void getAllReview_success(){
            
        }
    }
}

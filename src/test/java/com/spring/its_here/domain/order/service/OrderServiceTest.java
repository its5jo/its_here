package com.spring.its_here.domain.order.service;

import com.spring.its_here.domain.order.dto.request.OrderCreateRequestDto;
import com.spring.its_here.domain.order.dto.request.OrderProductRequestDto;
import com.spring.its_here.domain.order.dto.response.OrderResponseDto;
import com.spring.its_here.domain.order.entity.Order;
import com.spring.its_here.domain.order.repository.OrderProductRepository;
import com.spring.its_here.domain.order.repository.OrderRepository;
import com.spring.its_here.domain.payment.dto.response.PaymentResponseDto;
import com.spring.its_here.domain.payment.enums.PaymentMethod;
import com.spring.its_here.domain.payment.enums.PaymentStatus;
import com.spring.its_here.domain.payment.service.PaymentService;
import com.spring.its_here.domain.product.entity.Product;
import com.spring.its_here.domain.product.repository.ProductRepository;
import com.spring.its_here.domain.store.entity.Store;
import com.spring.its_here.domain.store.repository.StoreRepository;
import com.spring.its_here.domain.user.entity.UserEntity;
import com.spring.its_here.domain.user.enums.UserRole;
import com.spring.its_here.domain.user.repository.UserRepository;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private OrderProductRepository orderProductRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProductRepository productRepository;
    @Mock private StoreRepository storeRepository;
    @Mock private PaymentService paymentService;

    @InjectMocks private OrderService orderService;

    private static final Long USER_ID = 1L;
    private static final UUID STORE_ID = UUID.randomUUID();
    private static final UUID PRODUCT_ID = UUID.randomUUID();
    private static final UUID ORDER_ID = UUID.randomUUID();
    private static final UUID PAYMENT_ID = UUID.randomUUID();

    private UserEntity user;
    private Store store;
    private Product product;
    private OrderCreateRequestDto requestDto;
    private PaymentResponseDto paymentResponseDto;

    @BeforeEach
    void setUp() {
        user = UserEntity.create("testUser", "testPw", "테스터", UserRole.CUSTOMER);
        ReflectionTestUtils.setField(user, "id", USER_ID);

        store = mock(Store.class);

        product = Product.create("테스트상품", "설명", false, 10000, null);
        ReflectionTestUtils.setField(product, "id", PRODUCT_ID);

        requestDto = new OrderCreateRequestDto(
                STORE_ID,
                List.of(new OrderProductRequestDto(PRODUCT_ID, 2)),
                "서울시 강남구",
                "문 앞에 놔주세요",
                "CARD"
        );

        paymentResponseDto = new PaymentResponseDto(
                PAYMENT_ID, ORDER_ID, 20000, 20000,
                PaymentMethod.CARD, PaymentStatus.COMPLETED, null, Instant.now()
        );
    }

    @Nested
    @DisplayName("주문 생성 테스트")
    class CreateOrderTest {

        @Test
        @DisplayName("주문 생성 성공")
        void create_success() {
            // given
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
            when(storeRepository.findById(STORE_ID)).thenReturn(Optional.of(store));
            when(store.getHasOpen()).thenReturn(true);
            when(productRepository.findAllById(anyList())).thenReturn(List.of(product));
            when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
                Order order = invocation.getArgument(0);
                ReflectionTestUtils.setField(order, "id", ORDER_ID);
                return order;
            });
            when(orderProductRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
            when(paymentService.createForOrder(any(UUID.class), anyInt())).thenReturn(paymentResponseDto);

            // when
            OrderResponseDto response = orderService.create(requestDto, USER_ID);

            // then
            assertThat(response.orderId()).isEqualTo(ORDER_ID);
            assertThat(response.storeId()).isEqualTo(STORE_ID);
            assertThat(response.userId()).isEqualTo(USER_ID);
            assertThat(response.totalAmount()).isEqualTo(20000);
            assertThat(response.payment()).isEqualTo(paymentResponseDto);

            verify(userRepository).findById(USER_ID);
            verify(storeRepository).findById(STORE_ID);
            verify(productRepository).findAllById(anyList());
            verify(orderRepository).save(any(Order.class));
            verify(orderProductRepository).saveAll(anyList());
            verify(paymentService).createForOrder(ORDER_ID, 20000);
        }

        @Test
        @DisplayName("주문 생성 실패 - 존재하지 않는 유저")
        void create_fail_userNotFound() {
            // given
            when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

            // when
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> orderService.create(requestDto, USER_ID)
            );

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);

            verify(userRepository).findById(USER_ID);
            verifyNoInteractions(storeRepository, productRepository, orderRepository,
                    orderProductRepository, paymentService);
        }

        @Test
        @DisplayName("주문 생성 실패 - 존재하지 않는 가게")
        void create_fail_storeNotFound() {
            // given
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
            when(storeRepository.findById(STORE_ID)).thenReturn(Optional.empty());

            // when
            ResponseStatusException exception = assertThrows(
                    ResponseStatusException.class,
                    () -> orderService.create(requestDto, USER_ID)
            );

            // then
            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

            verify(storeRepository).findById(STORE_ID);
            verifyNoInteractions(productRepository, orderRepository, orderProductRepository, paymentService);
        }

        @Test
        @DisplayName("주문 생성 실패 - 삭제된 가게")
        void create_fail_storeDeleted() {
            // given
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
            when(storeRepository.findById(STORE_ID)).thenReturn(Optional.of(store));
            when(store.getDeletedAt()).thenReturn(Instant.now());

            // when
            ResponseStatusException exception = assertThrows(
                    ResponseStatusException.class,
                    () -> orderService.create(requestDto, USER_ID)
            );

            // then
            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

            verifyNoInteractions(productRepository, orderRepository, orderProductRepository, paymentService);
        }

        @Test
        @DisplayName("주문 생성 실패 - 영업 종료된 가게")
        void create_fail_storeClosed() {
            // given
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
            when(storeRepository.findById(STORE_ID)).thenReturn(Optional.of(store));
            when(store.getHasOpen()).thenReturn(false);

            // when
            ResponseStatusException exception = assertThrows(
                    ResponseStatusException.class,
                    () -> orderService.create(requestDto, USER_ID)
            );

            // then
            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

            verifyNoInteractions(productRepository, orderRepository, orderProductRepository, paymentService);
        }

        @Test
        @DisplayName("주문 생성 실패 - 존재하지 않는 상품 포함")
        void create_fail_productNotFound() {
            // given
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
            when(storeRepository.findById(STORE_ID)).thenReturn(Optional.of(store));
            when(store.getHasOpen()).thenReturn(true);
            when(productRepository.findAllById(anyList())).thenReturn(List.of());

            // when
            ResponseStatusException exception = assertThrows(
                    ResponseStatusException.class,
                    () -> orderService.create(requestDto, USER_ID)
            );

            // then
            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

            verifyNoInteractions(orderRepository, orderProductRepository, paymentService);
        }
    }
}
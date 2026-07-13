package com.spring.its_here.domain.order.service;

import com.spring.its_here.domain.order.dto.request.OrderCreateRequestDto;
import com.spring.its_here.domain.order.dto.request.OrderProductRequestDto;
import com.spring.its_here.domain.order.dto.response.OrderResponseDto;
import com.spring.its_here.domain.order.dto.response.OrderSummaryResponseDto;
import com.spring.its_here.domain.order.entity.Order;
import com.spring.its_here.domain.order.enums.OrderStatus;
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
import com.spring.its_here.global.response.PageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

        product = Product.create("테스트상품", "설명", false, 10000, null, store);
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
            when(storeRepository.findById(STORE_ID)).thenReturn(Optional.of(store));
            when(store.getDeletedAt()).thenReturn(null);
            when(store.getHasOpen()).thenReturn(true);
            when(productRepository.findAllById(anyList())).thenReturn(List.of(product));
            when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
                Order order = invocation.getArgument(0);
                ReflectionTestUtils.setField(order, "id", ORDER_ID);
                return order;
            });
            when(orderProductRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
            when(paymentService.createForOrder(any(UUID.class), anyInt())).thenReturn(paymentResponseDto);

            OrderResponseDto response = orderService.create(requestDto, USER_ID);

            assertThat(response.orderId()).isEqualTo(ORDER_ID);
            assertThat(response.totalAmount()).isEqualTo(20000);

            verify(storeRepository).findById(STORE_ID);
            verify(productRepository).findAllById(anyList());
            verify(orderRepository).save(any(Order.class));
            verify(orderProductRepository).saveAll(anyList());
            verify(paymentService).createForOrder(ORDER_ID, 20000);
        }

        @Test
        @DisplayName("주문 생성 실패 - 존재하지 않는 가게")
        void create_fail_storeNotFound() {
            when(storeRepository.findById(STORE_ID)).thenReturn(Optional.empty());

            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> orderService.create(requestDto, USER_ID)
            );

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.STORE_NOT_FOUND);
        }

        @Test
        @DisplayName("주문 생성 실패 - 삭제된 가게")
        void create_fail_storeDeleted() {
            when(storeRepository.findById(STORE_ID)).thenReturn(Optional.of(store));
            when(store.getDeletedAt()).thenReturn(Instant.now());

            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> orderService.create(requestDto, USER_ID)
            );

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.STORE_NOT_FOUND);
        }

        @Test
        @DisplayName("주문 생성 실패 - 영업 종료된 가게")
        void create_fail_storeClosed() {
            when(storeRepository.findById(STORE_ID)).thenReturn(Optional.of(store));
            when(store.getDeletedAt()).thenReturn(null);
            when(store.getHasOpen()).thenReturn(false);

            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> orderService.create(requestDto, USER_ID)
            );

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.STORE_CLOSED);
        }

        @Test
        @DisplayName("주문 생성 실패 - 존재하지 않는 상품 포함")
        void create_fail_productNotFound() {
            when(storeRepository.findById(STORE_ID)).thenReturn(Optional.of(store));
            when(store.getDeletedAt()).thenReturn(null);
            when(store.getHasOpen()).thenReturn(true);
            when(productRepository.findAllById(anyList())).thenReturn(List.of());

            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> orderService.create(requestDto, USER_ID)
            );

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);
        }
    }


        @Nested
    @DisplayName("주문 단건 조회 테스트")
    class GetOrderTest {

        private Order order;

        @BeforeEach
        void setUp() {
            order = Order.create(STORE_ID, USER_ID, "서울시 강남구", "문 앞에", 20000);
            ReflectionTestUtils.setField(order, "id", ORDER_ID);
        }

        @Test
        @DisplayName("단건 조회 성공 - CUSTOMER 본인 주문")
        void getOrder_success_customer() {
            // given
            when(orderRepository.findByIdAndDeletedAtIsNull(ORDER_ID))
                    .thenReturn(Optional.of(order));
            when(orderProductRepository.findAllByOrderId(ORDER_ID))
                    .thenReturn(List.of());
            when(paymentService.getPaymentByOrderId(ORDER_ID))
                    .thenReturn(paymentResponseDto);

            // when
            OrderResponseDto response = orderService.getOrder(ORDER_ID, USER_ID, UserRole.CUSTOMER);

            // then
            assertThat(response.orderId()).isEqualTo(ORDER_ID);
            assertThat(response.userId()).isEqualTo(USER_ID);
            verify(orderRepository).findByIdAndDeletedAtIsNull(ORDER_ID);
        }

        @Test
        @DisplayName("단건 조회 실패 - 존재하지 않는 주문")
        void getOrder_fail_orderNotFound() {
            // given
            when(orderRepository.findByIdAndDeletedAtIsNull(ORDER_ID))
                    .thenReturn(Optional.empty());

            // when
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> orderService.getOrder(ORDER_ID, USER_ID, UserRole.CUSTOMER)
            );

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ORDER_NOT_FOUND);
            verifyNoInteractions(orderProductRepository, paymentService);
        }

        @Test
        @DisplayName("단건 조회 실패 - CUSTOMER가 남의 주문 조회")
        void getOrder_fail_customerForbidden() {
            // given
            Long anotherUserId = 999L;
            when(orderRepository.findByIdAndDeletedAtIsNull(ORDER_ID))
                    .thenReturn(Optional.of(order));  // order.userId = USER_ID

            // when
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> orderService.getOrder(ORDER_ID, anotherUserId, UserRole.CUSTOMER)
            );

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ORDER_ACCESS_FORBIDDEN);
            verifyNoInteractions(orderProductRepository, paymentService);
        }

        @Test
        @DisplayName("단건 조회 성공 - MANAGER는 모든 주문 조회 가능")
        void getOrder_success_manager() {
            // given
            Long managerUserId = 999L;  // 다른 유저지만 MANAGER라 가능
            when(orderRepository.findByIdAndDeletedAtIsNull(ORDER_ID))
                    .thenReturn(Optional.of(order));
            when(orderProductRepository.findAllByOrderId(ORDER_ID))
                    .thenReturn(List.of());
            when(paymentService.getPaymentByOrderId(ORDER_ID))
                    .thenReturn(paymentResponseDto);

            // when
            OrderResponseDto response = orderService.getOrder(ORDER_ID, managerUserId, UserRole.MANAGER);

            // then
            assertThat(response.orderId()).isEqualTo(ORDER_ID);
            verify(orderRepository).findByIdAndDeletedAtIsNull(ORDER_ID);
        }
    }

    @Nested
    @DisplayName("주문 목록 조회 테스트")
    class GetOrderListTest {

        @Test
        @DisplayName("목록 조회 실패 - 허용되지 않는 페이지 크기")
        void getOrderList_fail_invalidPageSize() {
            // when
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> orderService.getOrderList(0, 20, USER_ID, UserRole.CUSTOMER, null)
            );

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_INPUT_VALUE);
            verifyNoInteractions(orderRepository);
        }

        @Test
        @DisplayName("목록 조회 성공 - CUSTOMER 허용 페이지 크기 10")
        void getOrderList_success_customer_size10() {
            // given
            Page<Order> emptyPage = Page.empty();
            when(orderRepository.findByUserIdAndDeletedAtIsNull(eq(USER_ID), any(Pageable.class)))
                    .thenReturn(emptyPage);

            // when
            PageResponse<OrderSummaryResponseDto> response =
                    orderService.getOrderList(0, 10, USER_ID, UserRole.CUSTOMER, null);

            // then
            assertThat(response.totalElements()).isEqualTo(0);
            verify(orderRepository).findByUserIdAndDeletedAtIsNull(eq(USER_ID), any(Pageable.class));
        }

        @Test
        @DisplayName("목록 조회 성공 - CUSTOMER status 필터")
        void getOrderList_success_customer_withStatus() {
            // given
            Page<Order> emptyPage = Page.empty();
            when(orderRepository.findByUserIdAndStatusAndDeletedAtIsNull(
                    eq(USER_ID), eq(OrderStatus.REQUESTED), any(Pageable.class)))
                    .thenReturn(emptyPage);

            // when
            PageResponse<OrderSummaryResponseDto> response =
                    orderService.getOrderList(0, 10, USER_ID, UserRole.CUSTOMER, OrderStatus.REQUESTED);

            // then
            assertThat(response.totalElements()).isEqualTo(0);
            verify(orderRepository).findByUserIdAndStatusAndDeletedAtIsNull(
                    eq(USER_ID), eq(OrderStatus.REQUESTED), any(Pageable.class));
        }

        @Test
        @DisplayName("목록 조회 성공 - MANAGER 전체 조회")
        void getOrderList_success_manager() {
            // given
            Page<Order> emptyPage = Page.empty();
            when(orderRepository.findByDeletedAtIsNull(any(Pageable.class)))
                    .thenReturn(emptyPage);

            // when
            PageResponse<OrderSummaryResponseDto> response =
                    orderService.getOrderList(0, 10, USER_ID, UserRole.MANAGER, null);

            // then
            verify(orderRepository).findByDeletedAtIsNull(any(Pageable.class));
        }
    }
}
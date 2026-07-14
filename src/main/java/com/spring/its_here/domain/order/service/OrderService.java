package com.spring.its_here.domain.order.service;

import com.spring.its_here.domain.order.dto.request.OrderCreateRequestDto;
import com.spring.its_here.domain.order.dto.request.OrderProductRequestDto;
import com.spring.its_here.domain.order.dto.response.OrderCancelResponseDto;
import com.spring.its_here.domain.order.dto.response.OrderListResponseDto;
import com.spring.its_here.domain.order.dto.response.OrderResponseDto;
import com.spring.its_here.domain.order.dto.response.OrderStatusResponseDto;
import com.spring.its_here.domain.order.entity.Order;
import com.spring.its_here.domain.order.entity.OrderProduct;
import com.spring.its_here.domain.order.enums.OrderStatus;
import com.spring.its_here.domain.order.repository.OrderProductRepository;
import com.spring.its_here.domain.order.repository.OrderRepository;
import com.spring.its_here.domain.payment.dto.response.PaymentResponseDto;
import com.spring.its_here.domain.payment.entity.Payment;
import com.spring.its_here.domain.payment.service.PaymentService;
import com.spring.its_here.domain.product.entity.Product;
import com.spring.its_here.domain.product.repository.ProductRepository;
import com.spring.its_here.domain.store.entity.Store;
import com.spring.its_here.domain.store.repository.StoreRepository;
import com.spring.its_here.domain.user.enums.UserRole;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final PaymentService paymentService;

    public OrderResponseDto create(OrderCreateRequestDto orderCreateRequestDto, Long userId) {
        validateStore(orderCreateRequestDto.storeId());
        Map<UUID, Product> productMap = validateProducts(orderCreateRequestDto.orderProducts())
                .stream().collect(Collectors.toMap(Product::getId, p -> p));

        // 총액 계산
        int totalAmount = calculateTotalAmount(orderCreateRequestDto.orderProducts(), productMap);

        // Order 생성
        Order order = Order.create(
                orderCreateRequestDto.storeId(),
                userId,
                orderCreateRequestDto.deliveryAddress(),
                orderCreateRequestDto.requestMemo(),
                totalAmount
        );
        orderRepository.save(order);

        // OrderProduct 생성
        List<OrderProduct> orderProducts = orderCreateRequestDto.orderProducts().stream()
                .map(item -> {
                    Product product = productMap.get(item.productId());
                    return OrderProduct.createSnapshot(
                            order, product.getId(), product.getName(), product.getPrice(), item.quantity()
                    );
                }).toList();
        orderProductRepository.saveAll(orderProducts);

        PaymentResponseDto payment = paymentService.createForOrder(order.getId(), totalAmount);
        return OrderResponseDto.from(order, orderProducts,payment);
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getOrder(UUID orderId, Long userId, UserRole role) {
        Order order = orderRepository.findByIdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> new ItsHereException(ErrorCode.ORDER_NOT_FOUND));

        // CUSTOMER - 본인 주문만 조회
        if (role == UserRole.CUSTOMER && !order.getUserId().equals(userId)) {
            throw new ItsHereException(ErrorCode.ORDER_ACCESS_FORBIDDEN);
        }

        // OWNER - 본인 가게 주문만 조회
        if (role == UserRole.OWNER) {
            Store store = storeRepository.findByUserIdAndDeletedAtIsNull(userId)
                    .orElseThrow(() -> new ItsHereException(ErrorCode.STORE_NOT_FOUND));
            if (!order.getStoreId().equals(store.getId())) {
                throw new ItsHereException(ErrorCode.ORDER_ACCESS_FORBIDDEN);
            }
        }

        List<OrderProduct> orderProducts = orderProductRepository.findAllByOrderId(orderId);
        PaymentResponseDto payment = paymentService.getPaymentByOrderId(orderId);

        return OrderResponseDto.from(order, orderProducts, payment);
    }

    @Transactional(readOnly = true)
    public OrderListResponseDto getOrderList(
            int page, int size, Long userId, UserRole role, OrderStatus orderStatus) {

        if (size != 10 && size != 30 && size != 50) {
            throw new ItsHereException(ErrorCode.INVALID_INPUT_VALUE);
        }

            Pageable pageable = PageRequest.of(page, size, Sort.by( "createdAt").descending());
            Page<Order> orders = switch (role) {
                case CUSTOMER -> orderStatus != null
                        ? orderRepository.findByUserIdAndStatusAndDeletedAtIsNull(userId, orderStatus,pageable)
                        : orderRepository.findByUserIdAndDeletedAtIsNull(userId, pageable);
                case OWNER -> {
                    Store store = storeRepository.findByUserIdAndDeletedAtIsNull(userId)
                            .orElseThrow(() -> new ItsHereException(ErrorCode.STORE_NOT_FOUND));
                    yield  orderRepository.findByStoreIdAndDeletedAtIsNull(store.getId(), pageable);
                    }
                case MANAGER, MASTER -> orderRepository.findByDeletedAtIsNull(pageable);
                };
        return OrderListResponseDto.from(orders);
    }

    public OrderCancelResponseDto cancelOrder(UUID orderId, Long userId, UserRole role) {
        // 주문 검증
        Order order = orderRepository.findByIdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> new ItsHereException(ErrorCode.ORDER_NOT_FOUND));

        // CUSTOMER, OWNER - 본인의 주문인지 확인
        if ((role == UserRole.CUSTOMER || role == UserRole.OWNER)
            && !order.getUserId().equals(userId)) {
            throw new ItsHereException(ErrorCode.ORDER_ACCESS_FORBIDDEN);
        }

        // 5분 이내의 주문인지 확인
        if (!order.isCancelable()) {
            throw new ItsHereException(ErrorCode.ORDER_CANCEL_TIMEOUT);
        }

        order.cancel();
        Payment payment = paymentService.cancelPayment(orderId);
        return OrderCancelResponseDto.from(order, payment);
    }

    @PreAuthorize("hasAnyAuthority('OWNER', 'MANAGER', 'MASTER')")
    public OrderStatusResponseDto updateStatus(UUID orderId, OrderStatus status,
                                               Long userId, UserRole role) {
        // 주문 검증
        Order order = orderRepository.findByIdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> new ItsHereException(ErrorCode.ORDER_NOT_FOUND));

        // OWNER - 본인 가게의 주문인지 확인
        if(role == UserRole.OWNER) {
            Store store =storeRepository.findByUserIdAndDeletedAtIsNull(userId)
                    .orElseThrow(() -> new ItsHereException(ErrorCode.STORE_NOT_FOUND));
            if (!order.getStoreId().equals(store.getId())) {
                throw new ItsHereException(ErrorCode.ORDER_ACCESS_FORBIDDEN);
            }
        }
        
        //상태 전이 검증
        if (!order.getStatus().canTransitionTo(status)) {
            throw new ItsHereException(ErrorCode.ORDER_STATUS_TRANSITION_INVALID);
        }
        order.updateStatus(status);
        return OrderStatusResponseDto.from(order);
    }


    // ===== 보조 메서드 =====
    private void validateStore(UUID storeId) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new ItsHereException(ErrorCode.STORE_NOT_FOUND));
        if (store.getDeletedAt() != null) {
            throw new ItsHereException(ErrorCode.STORE_NOT_FOUND);
        }

        if (!store.getHasOpen()) {
            throw new ItsHereException(ErrorCode.STORE_CLOSED);
        }
    }

    private List<Product> validateProducts(List<OrderProductRequestDto> items) {
        List<UUID> productIds = items.stream()
                .map(OrderProductRequestDto::productId)
                .toList();

        List<Product> products = productRepository.findAllById(productIds);

        if (products.size() != productIds.size()) {
            throw new ItsHereException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        return products;
    }

    private int calculateTotalAmount(List<OrderProductRequestDto> items,
                                     Map<UUID, Product> productMap) {
        return items.stream()
                .mapToInt(item -> productMap.get(item.productId()).getPrice() * item.quantity())
                .sum();
    }
}

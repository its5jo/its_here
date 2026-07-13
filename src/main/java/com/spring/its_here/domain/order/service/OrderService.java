package com.spring.its_here.domain.order.service;

import com.spring.its_here.domain.order.dto.request.OrderCreateRequestDto;
import com.spring.its_here.domain.order.dto.request.OrderProductRequestDto;
import com.spring.its_here.domain.order.dto.response.OrderResponseDto;
import com.spring.its_here.domain.order.entity.Order;
import com.spring.its_here.domain.order.entity.OrderProduct;
import com.spring.its_here.domain.order.repository.OrderProductRepository;
import com.spring.its_here.domain.order.repository.OrderRepository;
import com.spring.its_here.domain.payment.dto.response.PaymentResponseDto;
import com.spring.its_here.domain.payment.service.PaymentService;
import com.spring.its_here.domain.product.entity.Product;
import com.spring.its_here.domain.product.repository.ProductRepository;
import com.spring.its_here.domain.store.entity.Store;
import com.spring.its_here.domain.store.repository.StoreRepository;
import com.spring.its_here.domain.user.entity.UserEntity;
import com.spring.its_here.domain.user.repository.UserRepository;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final PaymentService paymentService;

    public OrderResponseDto create(OrderCreateRequestDto orderCreateRequestDto, Long userId) {
        validateUser(userId);
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

    // ===== 보조 메서드 =====
    private Store validateStore(UUID storeId) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "STORE_NOT_FOUND"));
        if (store.getDeletedAt() != null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "STORE_NOT_FOUND");
        }

        if (!store.getHasOpen()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "STORE_CLOSED");
        }
        return store;
    }

    private List<Product> validateProducts(List<OrderProductRequestDto> items) {
        List<UUID> productIds = items.stream()
                .map(OrderProductRequestDto::productId)
                .toList();

        List<Product> products = productRepository.findAllById(productIds);

        if (products.size() != productIds.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND");
        }
        return products;
    }

    private UserEntity validateUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ItsHereException(ErrorCode.USER_NOT_FOUND));
    }

    private int calculateTotalAmount(List<OrderProductRequestDto> items,
                                     Map<UUID, Product> productMap) {
        return items.stream()
                .mapToInt(item -> productMap.get(item.productId()).getPrice() * item.quantity())
                .sum();
    }
}

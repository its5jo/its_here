package com.spring.its_here.domain.order.dto.response;

import com.spring.its_here.domain.order.enums.OrderStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID orderId,
        UUID storeId,
        Long userId,
        OrderStatus status,
        int totalAmount,
        String deliveryAddress,
        String requestMemo,
        List<OrderProductResponse> orderProducts,
        PaymentResponse payment,
        Instant createdAt,
        Instant updatedAt
) {
}

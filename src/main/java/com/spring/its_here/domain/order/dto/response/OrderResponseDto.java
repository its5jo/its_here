package com.spring.its_here.domain.order.dto.response;

import com.spring.its_here.domain.order.enums.OrderStatus;
import com.spring.its_here.domain.payment.dto.response.PaymentResponseDto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponseDto(
        UUID orderId,
        UUID storeId,
        Long userId,
        OrderStatus status,
        int totalAmount,
        String deliveryAddress,
        String requestMemo,
        List<OrderProductResponseDto> orderProducts,
        PaymentResponseDto payment,
        Instant createdAt,
        Instant updatedAt
) {
}

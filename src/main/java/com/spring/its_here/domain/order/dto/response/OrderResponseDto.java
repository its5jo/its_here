package com.spring.its_here.domain.order.dto.response;

import com.spring.its_here.domain.order.entity.Order;
import com.spring.its_here.domain.order.entity.OrderProduct;
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
    public static OrderResponseDto from(Order order, List<OrderProduct> orderProducts, PaymentResponseDto payment) {
        return new OrderResponseDto(
                order.getId(),
                order.getStoreId(),
                order.getUserId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getDeliveryAddress(),
                order.getRequestMemo(),
                orderProducts.stream().map(OrderProductResponseDto::from).toList(),
                payment,
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}

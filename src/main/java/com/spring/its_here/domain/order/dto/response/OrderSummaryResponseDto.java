package com.spring.its_here.domain.order.dto.response;

import com.spring.its_here.domain.order.entity.Order;
import com.spring.its_here.domain.order.enums.OrderStatus;

import java.time.Instant;
import java.util.UUID;

public record OrderSummaryResponseDto(
        UUID orderId,
        UUID storeId,
        OrderStatus status,
        int totalAmount,
        Instant createdAt
) {
    public static OrderSummaryResponseDto from (Order order) {
        return new OrderSummaryResponseDto(
                order.getId(),
                order.getStoreId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreatedAt()
        );
    }
}

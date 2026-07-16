package com.spring.its_here.domain.order.dto.response;

import com.spring.its_here.domain.order.entity.Order;
import com.spring.its_here.domain.order.enums.OrderStatus;

import java.util.UUID;

public record OrderStatusResponseDto(
        UUID orderId,
        OrderStatus status
) {
    public static OrderStatusResponseDto from(Order order) {
        return new OrderStatusResponseDto(
                order.getId(),
                order.getStatus()
        );
    }
}

package com.spring.its_here.domain.order.dto.response;

import com.spring.its_here.domain.order.entity.OrderProduct;

import java.util.UUID;

public record OrderProductResponse(
        UUID orderProductId,
        UUID productId,
        String name,
        int price,
        long quantity
) {
    public static OrderProductResponse from(OrderProduct op) {
        return new OrderProductResponse(
                op.getId(),
                op.getProductId(),
                op.getName(),
                op.getPrice(),
                op.getQuantity()
        );
    }
}

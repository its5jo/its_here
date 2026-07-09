package com.spring.its_here.domain.order.dto.response;

import com.spring.its_here.domain.order.entity.OrderProduct;

import java.util.UUID;

public record OrderProductResponseDto(
        UUID orderProductId,
        UUID productId,
        String name,
        int price,
        int quantity
) {
    public static OrderProductResponseDto from(OrderProduct orderProduct) {
        return new OrderProductResponseDto(
                orderProduct.getId(),
                orderProduct.getProductId(),
                orderProduct.getName(),
                orderProduct.getPrice(),
                orderProduct.getQuantity()
        );
    }
}

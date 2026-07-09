package com.spring.its_here.domain.order.dto.request;

import com.spring.its_here.domain.order.enums.OrderStatus;

public record OrderUpdateRequestDto(
        OrderStatus status
) {
}

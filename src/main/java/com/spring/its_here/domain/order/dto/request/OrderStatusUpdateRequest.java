package com.spring.its_here.domain.order.dto.request;

import com.spring.its_here.domain.order.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record OrderStatusUpdateRequest(

        @NotNull(message = "변경할 상태 입력은 필수입니다")
        OrderStatus status
        ) {
}

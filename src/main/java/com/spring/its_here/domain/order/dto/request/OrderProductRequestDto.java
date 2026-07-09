package com.spring.its_here.domain.order.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OrderProductRequestDto(

        @NotNull(message = "상품 ID는 필수입니다")
        UUID productId,

        @Min(value = 1, message = "수량은 1개 이상이어야 합니다")
        int quantity
) {
}

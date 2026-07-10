package com.spring.its_here.domain.review.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ReviewCreateRequestDto(
        @NotNull(message = "주문 ID는 필수입니다.")
        UUID orderId,

        @NotNull(message = "평점 ID는 필수입니다.")
        @DecimalMin(
                value = "1.0",
                message = "평점은 1점 이상이어야 합니다."
        )
        @DecimalMax(
                value = "5.0",
                message = "평점은 5점 이하여야 합니다."
        )
        Double rating,

        @Size(
                max = 255,
                message = "리뷰 내용은 255자를 초과할 수 없습니다."
        )
        String content
) {
}

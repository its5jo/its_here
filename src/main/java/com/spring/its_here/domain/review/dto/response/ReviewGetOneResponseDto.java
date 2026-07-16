package com.spring.its_here.domain.review.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ReviewGetOneResponseDto(
        UUID reviewId,
        UUID orderId,
        UUID storeId,
        Long userId,
        Double rating,
        String content,
        Instant createdAt,
        Instant updatedAt
) {
}

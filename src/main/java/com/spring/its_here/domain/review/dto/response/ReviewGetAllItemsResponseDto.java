package com.spring.its_here.domain.review.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ReviewGetAllItemsResponseDto(
        UUID reviewId,
        Long userId,
        Double rating,
        String content,
        Instant createdAt
) {
}

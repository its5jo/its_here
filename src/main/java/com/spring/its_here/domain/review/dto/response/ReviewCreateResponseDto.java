package com.spring.its_here.domain.review.dto.response;

import java.util.UUID;

public record ReviewCreateResponseDto(
        UUID reviewId,
        UUID orderId,
        UUID storeId,
        Long userId
) {
}

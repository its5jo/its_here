package com.spring.its_here.domain.review.dto.response;

import java.util.UUID;

public record ReviewCreateResponseDto(
        UUID id,
        UUID orderId,
        UUID storeId,
        UUID userId
) {
}

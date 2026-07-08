package com.spring.its_here.domain.review.dto.request;

import java.util.UUID;

public record ReviewCreateRequestDto(
        UUID orderId,
        double rating,
        String content
) {
}

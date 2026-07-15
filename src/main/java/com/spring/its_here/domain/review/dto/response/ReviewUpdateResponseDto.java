package com.spring.its_here.domain.review.dto.response;

import java.util.UUID;

public record ReviewUpdateResponseDto(
        UUID reviewId
) {
    public static ReviewUpdateResponseDto from(UUID reviewId) {
        return new ReviewUpdateResponseDto(reviewId);
    }
}

package com.spring.its_here.domain.user.dto.response;

public record TokenPairDto(
        String accessToken,
        String refreshToken
) {
}

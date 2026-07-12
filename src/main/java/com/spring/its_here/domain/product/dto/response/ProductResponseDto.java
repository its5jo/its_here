package com.spring.its_here.domain.product.dto.response;

public record ProductResponseDto(
        String name,
        String description,
        boolean hasHidden,
        int price,
        String imageUrl
) {
}

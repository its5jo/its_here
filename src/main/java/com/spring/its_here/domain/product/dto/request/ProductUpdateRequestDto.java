package com.spring.its_here.domain.product.dto.request;

public record ProductUpdateRequestDto(
        String name,
        String description,
        Boolean hasHidden,
        Integer price,
        String imageUrl
) {
}

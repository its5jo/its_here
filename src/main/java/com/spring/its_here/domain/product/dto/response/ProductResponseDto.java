package com.spring.its_here.domain.product.dto.response;

import com.spring.its_here.domain.product.entity.Product;

public record ProductResponseDto(
        String name,
        String description,
        boolean hasHidden,
        int price,
        String imageUrl
) {
    public static ProductResponseDto from(Product product) {
        return new ProductResponseDto(
                product.getName(),
                product.getDescription(),
                product.isHasHidden(),
                product.getPrice(),
                product.getImageUrl()
        );

    }
}

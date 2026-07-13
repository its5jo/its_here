package com.spring.its_here.domain.product.dto.command;

import com.spring.its_here.domain.product.dto.request.ProductUpdateRequestDto;

import java.util.UUID;

public record ProductUpdateCommand(
        UUID productId,
        String name,
        String description,
        Boolean hasHidden,
        Integer price,
        String imageUrl
) {
    public static ProductUpdateCommand of(
            ProductUpdateRequestDto request,
            UUID productId
    ) {
        return new ProductUpdateCommand(
                productId,
                request.name(),
                request.description(),
                request.hasHidden(),
                request.price(),
                request.imageUrl()
        );
    }
}

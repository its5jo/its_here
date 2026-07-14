package com.spring.its_here.domain.product.dto.command;

import com.spring.its_here.domain.product.dto.request.ProductCreateRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record ProductCreateCommand(
        UUID storeId,
        String name,
        String description,
        int price,
        boolean hasHidden,
        boolean useAiDescription,
        MultipartFile image
) {
    public static ProductCreateCommand of(
            ProductCreateRequestDto request,
            MultipartFile image
    ) {
        return new ProductCreateCommand(
                request.storeId(),
                request.name(),
                request.description(),
                request.price(),
                request.hasHidden(),
                request.useAiDescription(),
                image
        );
    }
}

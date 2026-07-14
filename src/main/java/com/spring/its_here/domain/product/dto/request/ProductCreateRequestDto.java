package com.spring.its_here.domain.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ProductCreateRequestDto(

        @NotNull
        UUID storeId,

        @NotBlank
        @Size(max = 100)
        String name,

        @Size(max = 255)
        String description,

        @NotNull
        @PositiveOrZero
        Integer price,

        @NotNull
        Boolean hasHidden,

        @NotNull
        Boolean useAiDescription
) {
}

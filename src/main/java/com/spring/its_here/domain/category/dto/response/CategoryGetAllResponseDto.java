package com.spring.its_here.domain.category.dto.response;

import java.util.UUID;

public record CategoryGetAllResponseDto(
        UUID categoryId,
        String name,
        Boolean hasHidden
) {
}

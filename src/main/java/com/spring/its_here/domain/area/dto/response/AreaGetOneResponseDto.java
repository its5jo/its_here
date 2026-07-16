package com.spring.its_here.domain.area.dto.response;

import java.time.Instant;
import java.util.UUID;

public record AreaGetOneResponseDto(
        UUID areaId,
        String city,
        String district,
        String town,
        boolean hasAvailable,
        Instant createdAt
) {
}

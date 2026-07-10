package com.spring.its_here.domain.area.dto.response;

import java.util.UUID;

public record AreaCreateResponseDto(
        UUID areaId,
        boolean hasAvailable
) {
}

package com.spring.its_here.domain.area.dto.response;

import java.util.UUID;

public record AreaGetAllItemResponseDto(
        UUID areaId,
        String city,
        String district,
        String town,
        boolean hasAvailable
) {
}

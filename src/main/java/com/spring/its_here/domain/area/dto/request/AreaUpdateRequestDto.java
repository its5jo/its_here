package com.spring.its_here.domain.area.dto.request;

public record AreaUpdateRequestDto(
        String city,
        String district,
        String town,
        boolean hasAvailable
) {
}

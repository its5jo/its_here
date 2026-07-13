package com.spring.its_here.domain.area.dto.request;


public record AreaGetAllRequestDto(
        String city,
        String district,
        String town,
        Boolean hasAvailable
) {
}

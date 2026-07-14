package com.spring.its_here.domain.category.dto.request;

public record CategoryGetAllRequestDto(
        String name,
        Boolean hasHidden
) {
}

package com.spring.its_here.domain.area.dto.response;

public record AreaPageInfoResponseDto(
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext
) {
}

package com.spring.its_here.domain.store.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalTime;

public record StoreGetOneResponseDto(
        String name,
        String address,
        String area,
        String category,
        Double rating,
        Boolean hasOpen,

        @JsonFormat(pattern = "H:mm")
        LocalTime openAt,

        @JsonFormat(pattern = "H:mm")
        LocalTime closedAt
) {
}

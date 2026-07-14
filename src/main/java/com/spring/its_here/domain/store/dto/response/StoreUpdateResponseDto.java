package com.spring.its_here.domain.store.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spring.its_here.domain.store.entity.Store;

import java.time.LocalTime;

public record StoreUpdateResponseDto(
        String name,
        String address,
        String area,
        String category,
        Boolean hasOpen,

        @JsonFormat(pattern = "HH:mm")
        LocalTime openAt,

        @JsonFormat(pattern = "HH:mm")
        LocalTime closedAt
) {

    public static StoreUpdateResponseDto from(Store store) {

        return new StoreUpdateResponseDto(
                store.getName(),
                store.getAddress(),
                store.getArea().getTown(),
                store.getCategory().getName(),
                store.getHasOpen(),
                store.getOpenAt(),
                store.getClosedAt()
        );
    }
}

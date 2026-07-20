package com.spring.its_here.domain.store.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spring.its_here.domain.area.entity.Area;
import com.spring.its_here.domain.category.entity.Category;
import com.spring.its_here.domain.store.entity.Store;

import java.time.LocalTime;

public record StoreGetOneResponseDto(
        String name,
        String address,
        String area,
        String category,
        Boolean categoryHasHidden,
        Double rating,
        Boolean hasOpen,

        @JsonFormat(pattern = "HH:mm")
        LocalTime openAt,

        @JsonFormat(pattern = "HH:mm")
        LocalTime closedAt
) {

        public static StoreGetOneResponseDto from(Store store) {

                return new StoreGetOneResponseDto(
                        store.getName(),
                        store.getAddress(),
                        store.getArea().getTown(),
                        store.getCategory().getName(),
                        store.getCategory().isHasHidden(),
                        store.calculateAverageRating(),
                        store.getHasOpen(),
                        store.getOpenAt(),
                        store.getClosedAt()
                );
        }
}

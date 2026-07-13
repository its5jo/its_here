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
        Double rating,
        Boolean hasOpen,

        @JsonFormat(pattern = "H:mm")
        LocalTime openAt,

        @JsonFormat(pattern = "H:mm")
        LocalTime closedAt
) {

        public static StoreGetOneResponseDto from(Store store, Area area, Category category) {

                // (삭제됨) 처리
                String areaName = area.isDeleted() ? area.getTown() + "(삭제됨)" : area.getTown();
                String categoryName = category.isDeleted() ? category.getName() + "(삭제됨)" : category.getName();

                return new StoreGetOneResponseDto(
                        store.getName(),
                        store.getAddress(),
                        areaName,
                        categoryName,
                        store.calculateAverageRating(),
                        store.getHasOpen(),
                        store.getOpenAt(),
                        store.getClosedAt()
                );
        }
}

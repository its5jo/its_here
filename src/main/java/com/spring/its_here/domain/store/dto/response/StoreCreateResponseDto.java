package com.spring.its_here.domain.store.dto.response;

import java.util.UUID;

public record StoreCreateResponseDto(
        UUID storeId
) {

    public static StoreCreateResponseDto from(UUID storeId) {
        return new StoreCreateResponseDto(
                storeId
        );
    }

}

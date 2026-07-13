package com.spring.its_here.domain.store.dto.response;

import java.util.UUID;

public record StoreGetAllResponseDto(
        UUID storeId,
        String name,
        String category,
        String address,
        String area,
        Double rating,
        Boolean hasOpen
) {

}

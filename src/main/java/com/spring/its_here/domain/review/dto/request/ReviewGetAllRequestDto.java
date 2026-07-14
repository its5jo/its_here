package com.spring.its_here.domain.review.dto.request;

import java.util.UUID;

public record ReviewGetAllRequestDto(
        UUID storeId,
        Double rating
) {
}

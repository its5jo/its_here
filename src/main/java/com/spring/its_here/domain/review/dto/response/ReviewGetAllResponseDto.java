package com.spring.its_here.domain.review.dto.response;

import java.util.List;

public record ReviewGetAllResponseDto(
        List<ReviewGetAllItemsResponseDto> content,
        PageInfo pageInfo
) {
}

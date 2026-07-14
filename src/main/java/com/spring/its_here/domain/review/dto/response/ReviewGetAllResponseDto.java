package com.spring.its_here.domain.review.dto.response;

import com.spring.its_here.global.response.OffsetPageInfo;

import java.util.List;

public record ReviewGetAllResponseDto(
        List<ReviewGetAllItemsResponseDto> content,
        OffsetPageInfo pageInfo
) {
}

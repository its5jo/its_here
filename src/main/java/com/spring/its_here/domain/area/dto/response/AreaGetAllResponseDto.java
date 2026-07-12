package com.spring.its_here.domain.area.dto.response;


import java.util.List;

public record AreaGetAllResponseDto(
        List<AreaGetAllItemResponseDto> content,
        AreaPageInfoResponseDto pageInfo
) {
}

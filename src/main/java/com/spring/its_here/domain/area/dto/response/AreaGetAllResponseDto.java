package com.spring.its_here.domain.area.dto.response;


import com.spring.its_here.global.response.OffsetPageInfo;

import java.util.List;

public record AreaGetAllResponseDto(
        List<AreaGetAllItemResponseDto> content,
        OffsetPageInfo pageInfo
) {
}

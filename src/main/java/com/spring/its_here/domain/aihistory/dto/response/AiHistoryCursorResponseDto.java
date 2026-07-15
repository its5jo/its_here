package com.spring.its_here.domain.aihistory.dto.response;

import java.util.List;

public record AiHistoryCursorResponseDto(
        List<AiHistoryResponseDto> content,
        AiHistoryCursorPageInfo pageInfo
) {
}

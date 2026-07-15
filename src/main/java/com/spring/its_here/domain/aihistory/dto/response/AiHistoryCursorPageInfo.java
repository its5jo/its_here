package com.spring.its_here.domain.aihistory.dto.response;

import com.spring.its_here.domain.aihistory.enums.AiHistorySortDirection;

import java.util.UUID;

public record AiHistoryCursorPageInfo(
        String paginationType,
        String nextCursor,
        UUID nextIdAfter,
        boolean hasNext,
        String sortBy,
        AiHistorySortDirection sortDirection
) {

}

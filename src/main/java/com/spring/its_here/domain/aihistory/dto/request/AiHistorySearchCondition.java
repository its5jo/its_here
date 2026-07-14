package com.spring.its_here.domain.aihistory.dto.request;

import com.spring.its_here.domain.aihistory.enums.AiHistorySortCriteria;
import com.spring.its_here.domain.aihistory.enums.AiHistorySortDirection;

import java.util.UUID;

public record AiHistorySearchCondition(
        AiHistorySortCriteria sortBy,
        AiHistorySortDirection sortDirection,
        String cursor,
        UUID idAfter,
        Integer limit
) {
}

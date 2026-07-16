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
    public AiHistorySearchCondition {
        if (sortBy == null) {
            sortBy = AiHistorySortCriteria.CREATED_AT;
        }

        if (sortDirection == null) {
            sortDirection = AiHistorySortDirection.DESCENDING;
        }

        if (limit == null) {
            limit = 10;
        }

        limit = switch (limit) {
            case 10, 30, 50 -> limit;
            default -> 10;
        };
    }
}
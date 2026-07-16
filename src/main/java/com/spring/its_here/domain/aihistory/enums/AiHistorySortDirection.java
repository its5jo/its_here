package com.spring.its_here.domain.aihistory.enums;

import org.springframework.data.domain.Sort;

public enum AiHistorySortDirection {
    ASCENDING, DESCENDING;

    public Sort.Direction toSpringDirection() {
        return this == ASCENDING
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
    }
}

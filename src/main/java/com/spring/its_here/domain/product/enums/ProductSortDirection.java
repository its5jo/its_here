package com.spring.its_here.domain.product.enums;

import org.springframework.data.domain.Sort;

public enum ProductSortDirection {
    ASCENDING, DESCENDING;

    public Sort.Direction toSpringDirection() {
        return this == ASCENDING
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
    }
}

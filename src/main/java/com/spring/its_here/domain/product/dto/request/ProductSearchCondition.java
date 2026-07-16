package com.spring.its_here.domain.product.dto.request;

import com.spring.its_here.domain.product.enums.ProductSortCriteria;
import com.spring.its_here.domain.product.enums.ProductSortDirection;

import java.util.UUID;

public record ProductSearchCondition(
        ProductSortCriteria sortBy,
        ProductSortDirection sortDirection,
        String cursor,
        UUID idAfter,
        Integer limit
) {
    public ProductSearchCondition {
        if (sortBy == null) {
            sortBy = ProductSortCriteria.CREATED_AT;
        }

        if (sortDirection == null) {
            sortDirection = ProductSortDirection.DESCENDING;
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

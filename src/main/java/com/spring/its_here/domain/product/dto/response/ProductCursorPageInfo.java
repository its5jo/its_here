package com.spring.its_here.domain.product.dto.response;


import com.spring.its_here.domain.product.enums.ProductSortDirection;

import java.util.UUID;

public record ProductCursorPageInfo(
        String paginationType,
        String nextCursor,
        UUID nextIdAfter,
        boolean hasNext,
        String sortBy,
        ProductSortDirection sortDirection
) {
}

package com.spring.its_here.global.response;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

public record OffsetPageInfo(
        String paginationType,
        Boolean hasNext,
        Long totalCount,
        String sortBy,
        String sortDirection
) {

    public static OffsetPageInfo from(Page<?> page) {

        Sort.Order order = page.getSort().stream()
                .findFirst()
                .orElse(Sort.Order.asc("createdAt"));

        return new OffsetPageInfo(
                "OFFSET",
                page.hasNext(),
                page.getTotalElements(),
                order.getProperty(),
                order.getDirection().name()
        );
    }
}
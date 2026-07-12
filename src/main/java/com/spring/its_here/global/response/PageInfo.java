package com.spring.its_here.global.response;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

public record PageInfo(
        String paginationType,
        boolean hasNext,
        long totalCount,
        String sortBy,
        String sortDirection
) {

    public static PageInfo from(Page<?> page) {

        Sort.Order order = page.getSort().stream()
                .findFirst()
                .orElse(Sort.Order.asc("createdAt"));

        return new PageInfo(
                "OFFSET",
                page.hasNext(),
                page.getTotalElements(),
                order.getProperty(),
                order.getDirection().name()
        );
    }
}
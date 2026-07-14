package com.spring.its_here.domain.address.pageable;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class PageablePolicy {
    private static final int DEFAULT_SIZE = 10;
    private static final String DEFAULT_SORT = "createdAt";

    private static final Set<Integer> ALLOWED_SIZE =
            Set.of(10, 30, 50);

    public Pageable normalize(
            Pageable pageable,
            Set<String> allowedSortProperties
    ) {

        int size =
                ALLOWED_SIZE.contains(pageable.getPageSize())
                        ? pageable.getPageSize()
                        : DEFAULT_SIZE;

        Sort.Order order =
                pageable.getSort()
                        .stream()
                        .findFirst()
                        .orElse(Sort.Order.desc(DEFAULT_SORT));

        String property =
                allowedSortProperties.contains(order.getProperty())
                        ? order.getProperty()
                        : DEFAULT_SORT;

        return PageRequest.of(
                pageable.getPageNumber(),
                size,
                Sort.by(order.getDirection(), property)
        );
    }
}

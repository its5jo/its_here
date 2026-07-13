package com.spring.its_here.domain.product.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ProductSortCriteria {
    CREATED_AT("createdAt");

    private final String value;

    public static ProductSortCriteria from(String value) {
        return Arrays.stream(values())
                .filter(productSortCriteria -> productSortCriteria.value.equals(value))
                .findFirst()
                .orElseThrow();
    }
}

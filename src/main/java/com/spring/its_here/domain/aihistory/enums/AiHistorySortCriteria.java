package com.spring.its_here.domain.aihistory.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum AiHistorySortCriteria {
    CREATED_AT("createdAt");

    private final String value;

    public static AiHistorySortCriteria from(String value) {
        return Arrays.stream(values())
                .filter(aiHistorySortCriteria -> aiHistorySortCriteria.value.equals(value))
                .findFirst()
                .orElseThrow();
    }
}

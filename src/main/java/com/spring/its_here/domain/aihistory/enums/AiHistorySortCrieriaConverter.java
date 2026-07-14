package com.spring.its_here.domain.aihistory.enums;

import org.springframework.core.convert.converter.Converter;

public class AiHistorySortCrieriaConverter implements Converter<String, AiHistorySortCriteria> {
    @Override
    public AiHistorySortCriteria convert(String source) {
        return AiHistorySortCriteria.from(source);
    }
}

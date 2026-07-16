package com.spring.its_here.domain.product.enums;

import org.springframework.core.convert.converter.Converter;

public class ProductSortCriteriaConverter implements Converter<String, ProductSortCriteria> {
    @Override
    public ProductSortCriteria convert(String source) {
        return ProductSortCriteria.from(source);
    }
}

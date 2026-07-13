package com.spring.its_here.domain.product.dto.response;

import java.util.List;

public record ProductCursorResult<T>(
        List<T> data,
        boolean hasNext
) {

}

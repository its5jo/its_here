package com.spring.its_here.domain.product.dto.response;

import java.util.List;

public record ProductCursorResponseDto(
        List<ProductResponseDto> content,
        ProductCursorPageInfo pageInfo
) {

}

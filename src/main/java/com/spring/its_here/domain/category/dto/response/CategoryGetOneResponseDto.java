package com.spring.its_here.domain.category.dto.response;

import com.spring.its_here.domain.category.entity.Category;

public record CategoryGetOneResponseDto(
        String name,
        Boolean hasHidden
) {

    public static CategoryGetOneResponseDto from(Category category){
        return new CategoryGetOneResponseDto(category.getName(), category.isHasHidden());
    }

}

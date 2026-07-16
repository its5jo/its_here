package com.spring.its_here.domain.category.dto.response;

import com.spring.its_here.domain.category.entity.Category;

public record CategoryUpdateResponseDto(
        String name,
        Boolean hasHidden
) {

public static CategoryUpdateResponseDto from(Category category){
    return new CategoryUpdateResponseDto(category.getName(), category.isHasHidden());
}

}

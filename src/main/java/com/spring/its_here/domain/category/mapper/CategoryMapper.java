package com.spring.its_here.domain.category.mapper;

import com.spring.its_here.domain.category.dto.request.CategoryCreateRequestDto;
import com.spring.its_here.domain.category.dto.response.CategoryCreateResponseDto;
import com.spring.its_here.domain.category.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toEntity(CategoryCreateRequestDto dto){
        return new Category(dto.name(), dto.hasHidden());
    }

    public CategoryCreateResponseDto toCreateResponseDto(Category category){
        return new CategoryCreateResponseDto(category.getId());
    }

}

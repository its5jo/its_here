package com.spring.its_here.domain.category.service;

import com.spring.its_here.domain.category.dto.request.CategoryCreateRequestDto;
import com.spring.its_here.domain.category.dto.response.CategoryCreateResponseDto;
import com.spring.its_here.domain.category.entity.Category;
import com.spring.its_here.domain.category.repository.CategoryRepository;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import com.spring.its_here.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @PreAuthorize("hasAnyAuthority('MANAGER','MASTER')")
    @Transactional
    public CategoryCreateResponseDto createCategory(
            CustomUserDetails userDetails, CategoryCreateRequestDto requestDto) {

        validateCategoryCreate(requestDto);

        Category category = Category.createCategory(requestDto.name(), requestDto.hasHidden());

        Category savedCategory = categoryRepository.save(category);
        return new CategoryCreateResponseDto(savedCategory.getId());
    }

    private void validateCategoryCreate(CategoryCreateRequestDto requestDto) {
        if (categoryRepository.existsByNameAndDeletedAtIsNull(requestDto.name())) {
            throw new ItsHereException(ErrorCode.CATEGORY_NAME_DUPLICATE);
        }
    }

}

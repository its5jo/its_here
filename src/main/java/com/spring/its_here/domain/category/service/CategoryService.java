package com.spring.its_here.domain.category.service;

import com.spring.its_here.domain.category.dto.request.CategoryCreateRequestDto;
import com.spring.its_here.domain.category.dto.response.CategoryCreateResponseDto;
import com.spring.its_here.domain.category.entity.Category;
import com.spring.its_here.domain.category.mapper.CategoryMapper;
import com.spring.its_here.domain.category.repository.CategoryRepository;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryCreateResponseDto createCategory(CategoryCreateRequestDto requestDto) {

        // 동일한 카테고리가 존재하는지 확인
        if (categoryRepository.existsByNameAndHasDeletedFalse(requestDto.name())) {
            throw new ItsHereException(ErrorCode.DUPLICATE_CATEGORY_NAME);
        }

        Category category = categoryMapper.toEntity(requestDto);
        Category createdCategory = categoryRepository.save(category);
        return categoryMapper.toCreateResponseDto(createdCategory);
    }

}

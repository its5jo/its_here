package com.spring.its_here.domain.category.service;

import com.spring.its_here.domain.category.dto.request.CategoryCreateRequestDto;
import com.spring.its_here.domain.category.dto.request.CategoryGetAllRequestDto;
import com.spring.its_here.domain.category.dto.response.CategoryCreateResponseDto;
import com.spring.its_here.domain.category.dto.response.CategoryGetAllResponseDto;
import com.spring.its_here.domain.category.dto.response.CategoryGetOneResponseDto;
import com.spring.its_here.domain.category.entity.Category;
import com.spring.its_here.domain.category.repository.CategoryRepository;
import com.spring.its_here.domain.user.enums.UserRole;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import com.spring.its_here.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @PreAuthorize("hasAnyAuthority('MANAGER','MASTER')")
    @Transactional
    public CategoryCreateResponseDto createCategory(CategoryCreateRequestDto requestDto) {

        existsByNameAndDeletedAtIsNull(requestDto);

        Category category = Category.createCategory(requestDto.name(), requestDto.hasHidden());

        Category savedCategory = categoryRepository.save(category);
        return new CategoryCreateResponseDto(savedCategory.getId());
    }

    public CategoryGetOneResponseDto getOneCategory(UUID categoryId) {
        Category category = findCategoryByIdAndNotDeleted(categoryId);
        return CategoryGetOneResponseDto.from(category);
    }

    public Page<CategoryGetAllResponseDto> getAllCategories(
            CategoryGetAllRequestDto requestDto, Pageable pageable
    ) {
        Pageable newPageable = validatePageable(pageable);
        return categoryRepository.getAllCategories(requestDto.name(), requestDto.hasHidden(), newPageable);
    }

    private void existsByNameAndDeletedAtIsNull(CategoryCreateRequestDto requestDto) {
        if (categoryRepository.existsByNameAndDeletedAtIsNull(requestDto.name())) {
            throw new ItsHereException(ErrorCode.CATEGORY_NAME_DUPLICATE);
        }
    }

    private Category findCategoryByIdAndNotDeleted(UUID categoryId){
        return categoryRepository.findByIdAndDeletedAtIsNull(categoryId)
                .orElseThrow(() -> new ItsHereException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    private Pageable validatePageable(Pageable pageable) {
        int size = pageable.getPageSize();

        if (size == 10 || size == 30 || size == 50) {
            return pageable;
        }

        return PageRequest.of(
                pageable.getPageNumber(),
                10,
                pageable.getSort()
        );
    }
}

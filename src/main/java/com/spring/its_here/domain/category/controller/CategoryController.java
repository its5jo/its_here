package com.spring.its_here.domain.category.controller;

import com.spring.its_here.domain.category.controller.docs.CategoryApi;
import com.spring.its_here.domain.category.dto.request.CategoryCreateRequestDto;
import com.spring.its_here.domain.category.dto.request.CategoryGetAllRequestDto;
import com.spring.its_here.domain.category.dto.request.CategoryUpdateRequestDto;
import com.spring.its_here.domain.category.dto.response.*;
import com.spring.its_here.domain.category.service.CategoryService;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import com.spring.its_here.global.response.ApiResponse;
import com.spring.its_here.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RequestMapping("/api/categories")
@RestController
@RequiredArgsConstructor
public class CategoryController implements CategoryApi {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryCreateResponseDto>> createCategory(@Valid @RequestBody CategoryCreateRequestDto requestDto) {
        CategoryCreateResponseDto responseDto = categoryService.createCategory(requestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("카테고리 등록 성공", responseDto));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryGetOneResponseDto>> getOneCategory(@PathVariable UUID categoryId) {
        CategoryGetOneResponseDto responseDto = categoryService.getOneCategory(categoryId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("카테고리 조회 성공", responseDto));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CategoryGetAllPageResponseDto>> getAllCategory(
            @ModelAttribute CategoryGetAllRequestDto requestDto,
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        Pageable validatedPageable = validatePageable(pageable);
        Page<CategoryGetAllResponseDto> responseDtoList = categoryService.getAllCategories(requestDto, validatedPageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("카테고리 목록 조회 성공"
                        , CategoryGetAllPageResponseDto.from(responseDtoList)));
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryUpdateResponseDto>> updateCategory(
            @PathVariable UUID categoryId,
            CategoryUpdateRequestDto requestDto) {
        CategoryUpdateResponseDto responseDto = categoryService.updateCategory(categoryId, requestDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("카테고리 수정 성공", responseDto));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID categoryId) {
        categoryService.deleteCategory(userDetails, categoryId);
        return ResponseEntity
                .noContent()
                .build();
    }

    private Pageable validatePageable(Pageable pageable) {
        Set<String> availableSortFields = Set.of("createdAt", "name");

        Sort sort = pageable.getSort();
        for(Sort.Order order : sort){
            String property = order.getProperty();
            if (!availableSortFields.contains(property)) {
                throw new ItsHereException(ErrorCode.CATEGORY_INVALID_SORT_FIELD);
            }
        }

        int size = pageable.getPageSize();
        if ((size == 10 || size == 30 || size == 50)) {
            return pageable;
        }

        return PageRequest.of(
                pageable.getPageNumber(),
                10,
                sort
        );
    }

}
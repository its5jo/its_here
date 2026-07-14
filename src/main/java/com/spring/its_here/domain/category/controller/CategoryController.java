package com.spring.its_here.domain.category.controller;

import com.spring.its_here.domain.category.dto.request.CategoryCreateRequestDto;
import com.spring.its_here.domain.category.dto.request.CategoryGetAllRequestDto;
import com.spring.its_here.domain.category.dto.response.CategoryCreateResponseDto;
import com.spring.its_here.domain.category.dto.response.CategoryGetAllPageResponseDto;
import com.spring.its_here.domain.category.dto.response.CategoryGetAllResponseDto;
import com.spring.its_here.domain.category.dto.response.CategoryGetOneResponseDto;
import com.spring.its_here.domain.category.service.CategoryService;
import com.spring.its_here.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/categories")
@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryCreateResponseDto>> createCategory(
            @Valid @RequestBody CategoryCreateRequestDto requestDto) {
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
        Page<CategoryGetAllResponseDto> responseDtoList = categoryService.getAllCategories(requestDto, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("카테고리 목록 조회 성공"
                        , CategoryGetAllPageResponseDto.from(responseDtoList)));
    }

}

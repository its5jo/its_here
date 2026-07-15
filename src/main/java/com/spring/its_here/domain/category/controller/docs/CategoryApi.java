package com.spring.its_here.domain.category.controller.docs;

import com.spring.its_here.domain.category.dto.response.CategoryCreateResponseDto;
import com.spring.its_here.domain.category.dto.response.CategoryGetAllPageResponseDto;
import com.spring.its_here.domain.category.dto.response.CategoryGetOneResponseDto;
import com.spring.its_here.domain.store.dto.response.StoreUpdateResponseDto;
import com.spring.its_here.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Category", description = "카테고리 API")
public interface CategoryApi {
    ResponseEntity<ApiResponse<CategoryCreateResponseDto>> createCategory();

    ResponseEntity<ApiResponse<CategoryGetOneResponseDto>> getOneCategory();

    ResponseEntity<ApiResponse<CategoryGetAllPageResponseDto>> getAllCategory();

    ResponseEntity<ApiResponse<StoreUpdateResponseDto>> updateCategory();

    ResponseEntity<Void> deleteCategory();
}

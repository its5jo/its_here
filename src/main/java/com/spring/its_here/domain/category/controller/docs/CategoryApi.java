package com.spring.its_here.domain.category.controller.docs;

import com.spring.its_here.domain.category.dto.request.CategoryCreateRequestDto;
import com.spring.its_here.domain.category.dto.request.CategoryGetAllRequestDto;
import com.spring.its_here.domain.category.dto.request.CategoryUpdateRequestDto;
import com.spring.its_here.domain.category.dto.response.CategoryCreateResponseDto;
import com.spring.its_here.domain.category.dto.response.CategoryGetAllPageResponseDto;
import com.spring.its_here.domain.category.dto.response.CategoryGetOneResponseDto;
import com.spring.its_here.domain.category.dto.response.CategoryUpdateResponseDto;
import com.spring.its_here.global.advice.ErrorResponse;
import com.spring.its_here.global.response.ApiResponse;
import com.spring.its_here.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Category", description = "카테고리 API")
public interface CategoryApi {

    @Operation(
            summary = "카테고리 등록",
            description = "관리자가 새로운 카테고리를 등록합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "카테고리 등록 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "잘못된 카테고리 등록 정보",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "카테고리 등록 권한 없음",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "409",
                            description = "동일한 이름의 카테고리가 존재함",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    ResponseEntity<ApiResponse<CategoryCreateResponseDto>> createCategory(
            @Valid
            @RequestBody
            @Parameter(description = "카테고리 등록 정보", required = true)
            CategoryCreateRequestDto requestDto
    );

    @Operation(
            summary = "카테고리 단건 조회",
            description = "카테고리 ID를 이용해 카테고리 정보를 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "카테고리 조회 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "카테고리를 찾을 수 없음",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    ResponseEntity<ApiResponse<CategoryGetOneResponseDto>> getOneCategory(
            @PathVariable
            @Parameter(
                    description = "카테고리 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            UUID categoryId
    );

    @Operation(
            summary = "카테고리 목록 조회",
            description = """
                    카테고리 목록을 페이지 기반으로 조회합니다.
                    카테고리 이름과 숨김 여부로 조회할 수 있습니다.
                    정렬 기준은 createdAt과 name만 허용됩니다.
                    페이지 크기는 10, 30, 50이 아닌 그 외의 값은 10으로 변경됩니다.
                    """,
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "카테고리 목록 조회 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "허용되지 않은 정렬 기준",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    ResponseEntity<ApiResponse<CategoryGetAllPageResponseDto>> getAllCategory(
            @ModelAttribute
            CategoryGetAllRequestDto requestDto,
            Pageable pageable
    );

    @Operation(
            summary = "카테고리 수정",
            description = "관리자가 카테고리 정보를 수정합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "카테고리 수정 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "잘못된 카테고리 수정 정보",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "카테고리 수정 권한 없음",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "카테고리를 찾을 수 없음",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "409",
                            description = "동일한 이름의 카테고리가 존재함",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    ResponseEntity<ApiResponse<CategoryUpdateResponseDto>> updateCategory(
            @PathVariable
            @Parameter(
                    description = "카테고리 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            UUID categoryId,

            @Valid
            @RequestBody
            @Parameter(
                    description = "카테고리 수정 정보",
                    required = true
            )
            CategoryUpdateRequestDto requestDto
    );

    @Operation(
            summary = "카테고리 삭제",
            description = "관리자가 카테고리를 삭제합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "카테고리 삭제 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "카테고리 삭제 권한 없음",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "카테고리를 찾을 수 없음",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    ResponseEntity<Void> deleteCategory(
            @Parameter(hidden = true)
            CustomUserDetails userDetails,

            @PathVariable
            @Parameter(
                    description = "카테고리 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            UUID categoryId
    );
}
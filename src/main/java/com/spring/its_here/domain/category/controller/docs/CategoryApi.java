package com.spring.its_here.domain.category.controller.docs;

import com.spring.its_here.domain.category.dto.request.CategoryCreateRequestDto;
import com.spring.its_here.domain.category.dto.request.CategoryGetAllRequestDto;
import com.spring.its_here.domain.category.dto.response.CategoryCreateResponseDto;
import com.spring.its_here.domain.category.dto.response.CategoryGetAllPageResponseDto;
import com.spring.its_here.domain.category.dto.response.CategoryGetOneResponseDto;
import com.spring.its_here.global.advice.ErrorResponse;
import com.spring.its_here.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Category", description = "카테고리 API")
public interface CategoryApi {
    // TODO - 1 : .env에 GEMINI_API_KEY 추가하셔야 실행됩니다 참고바람.
    // TODO - 2 : Category의 모든 API를 구현하신 후 상속 받을 것, 에러남
    // TODO - 3 : 주석 지울 것, 헷갈리실까봐 달아놓았습니다.

    // 생성
    @Operation(
            summary = "카테고리 등록",
            description = "관리자가 새로운 카테고리를 등록합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "201",
                            description = "카테고리 등록 성공"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "잘못된 카테고리 등록 정보",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "카테고리 등록 권한 없음",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "409",
                            description = "동일한 이름의 카테고리가 존재함",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<ApiResponse<CategoryCreateResponseDto>> createCategory(
            @Parameter(
                    description = "카테고리 등록 정보",
                    required = true
            )
            CategoryCreateRequestDto requestDto
    );

    // 단건조회
    @Operation(
            summary = "카테고리 단건 조회",
            description = "카테고리 ID를 이용해 카테고리 정보를 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "카테고리 조회 성공"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "카테고리를 찾을 수 없음",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<ApiResponse<CategoryGetOneResponseDto>> getOneCategory(
            @Parameter(
                    description = "카테고리 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            UUID categoryId
    );

    // 전체조회
    @Operation(
            summary = "카테고리 목록 조회",
            description = """
                    카테고리 목록을 페이지 기반으로 조회합니다.
                    카테고리 이름과 숨김 여부로 조회할 수 있습니다.
                    정렬 기준은 createdAt과 name만 허용됩니다.
                    페이지 크기는 10, 30, 50이 아닌 그 외의 값은 10으로 변경됩니다.
                    """,
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "카테고리 목록 조회 성공"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "허용되지 않은 정렬 기준",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<ApiResponse<CategoryGetAllPageResponseDto>> getAllCategory(
            @Parameter(
                    description = "카테고리 조회 조건",
                    required = false
            )
            CategoryGetAllRequestDto requestDto,

            @Parameter(
                    description = "페이지 및 정렬 정보",
                    required = true
            )
            Pageable pageable
    );

    // 수정
    @Operation(
            summary = "카테고리 수정",
            description = "관리자가 카테고리 정보를 수정합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "카테고리 수정 성공"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "잘못된 카테고리 수정 정보",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "카테고리 수정 권한 없음",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "카테고리를 찾을 수 없음",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "409",
                            description = "동일한 이름의 카테고리가 존재함",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    // TODO : 구현 후, "Void" -> CategoryUpdateResponseDto로 변경할 것
    ResponseEntity<Void> updateCategory(
            @Parameter(
                    description = "카테고리 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            UUID categoryId,

            @Parameter(
                    description = "카테고리 수정 정보",
                    required = true
            )
            // TODO : Void -> CategoryUpdateResponseDto로 변경할 것
            Void requestDto
    );

    // 삭제
    @Operation(
            summary = "카테고리 삭제",
            description = "관리자가 카테고리를 삭제합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "204",
                            description = "카테고리 삭제 성공"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "카테고리 삭제 권한 없음",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "카테고리를 찾을 수 없음",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<Void> deleteCategory(
            @Parameter(
                    description = "카테고리 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            UUID categoryId
    );
}

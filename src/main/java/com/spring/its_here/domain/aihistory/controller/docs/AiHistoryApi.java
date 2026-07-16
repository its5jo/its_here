package com.spring.its_here.domain.aihistory.controller.docs;

import com.spring.its_here.domain.aihistory.dto.request.AiHistorySearchCondition;
import com.spring.its_here.domain.aihistory.dto.response.AiHistoryCursorResponseDto;
import com.spring.its_here.domain.aihistory.dto.response.AiHistoryResponseDto;
import com.spring.its_here.global.advice.ErrorResponse;
import com.spring.its_here.global.response.ApiResponse;
import com.spring.its_here.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "AI History", description = "AI 기록 조회 API")
public interface AiHistoryApi {

    @Operation(
            summary = "AI 기록 단건 조회",
            description = "AI 기록 ID를 이용하여 AI 요청/응답 기록을 조회합니다. MANAGER와 MASTER만 조회할 수 있습니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "AI 기록 조회 성공"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "조회 권한 없음",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "사용자 또는 AI 기록을 찾을 수 없음",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<ApiResponse<AiHistoryResponseDto>> getAiHistory(
            @Parameter(hidden = true)
            CustomUserDetails userDetails,

            @Parameter(
                    description = "AI 기록 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            UUID aiHistoryId
    );

    @Operation(
            summary = "AI 기록 전체 조회",
            description = "전체 AI 요청/응답 기록을 조회합니다. MANAGER와 MASTER만 조회할 수 있습니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "AI 기록 전체 조회 성공"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "조회 권한 없음",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "사용자를 찾을 수 없음",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<ApiResponse<List<AiHistoryResponseDto>>> getAiHistories(
            @Parameter(hidden = true)
            CustomUserDetails userDetails
    );

    @Operation(
            summary = "상품별 AI 기록 목록 조회",
            description = """
                    상품 ID를 이용해 해당 상품의 AI 기록을 커서 기반으로 조회합니다.
                    커서가 없는 경우 첫 페이지를 조회합니다.
                    MANAGER와 MASTER만 조회할 수 있습니다.
                    """,
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "상품 AI 기록 목록 조회 성공"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "잘못된 조회 조건",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "조회 권한 없음",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "사용자 또는 상품을 찾을 수 없음",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<ApiResponse<AiHistoryCursorResponseDto>> getProductAiHistories(
            @Parameter(hidden = true)
            CustomUserDetails userDetails,

            @ParameterObject
            AiHistorySearchCondition condition,

            @Parameter(
                    description = "상품 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            UUID productId
    );
}
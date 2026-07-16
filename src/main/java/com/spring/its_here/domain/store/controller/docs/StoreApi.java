package com.spring.its_here.domain.store.controller.docs;

import com.spring.its_here.domain.store.dto.request.StoreCreateRequestDto;
import com.spring.its_here.domain.store.dto.request.StoreUpdateRequestDto;
import com.spring.its_here.domain.store.dto.response.StoreCreateResponseDto;
import com.spring.its_here.domain.store.dto.response.StoreGetAllPageResponseDto;
import com.spring.its_here.domain.store.dto.response.StoreGetOneResponseDto;
import com.spring.its_here.domain.store.dto.response.StoreUpdateResponseDto;
import com.spring.its_here.global.advice.ErrorResponse;
import com.spring.its_here.global.response.ApiResponse;
import com.spring.its_here.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.UUID;


@Tag(name = "Store", description = "가게 API")
public interface StoreApi {
    // 생성
    @Operation(
            summary = "가게 등록",
            description = "가게 주인 또는 관리자가 새로운 가게를 등록합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "201",
                            description = "가게 등록 성공"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "잘못된 가게 등록 정보",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "가게 등록 권한 없음",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "지역 또는 카테고리를 찾을 수 없음",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "409",
                            description = "동일한 이름의 가게가 존재하거나 이미 가게를 등록함",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "409",
                            description = "이미 하나의 가게를 등록함",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<ApiResponse<StoreCreateResponseDto>> createStore(
            @Parameter(hidden = true)
            CustomUserDetails userDetails,

            @Parameter(
                    description = "가게 등록 정보",
                    required = true
            )
            StoreCreateRequestDto requestDto
    );

    // 단건 조회
    @Operation(
            summary = "가게 단건 조회",
            description = "가게 ID를 이용해 가게 정보를 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "가게 조회 성공"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "고객이나 다른 가게의 주인인 경우",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "가게를 찾을 수 없음",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<ApiResponse<StoreGetOneResponseDto>> getOneStore(
            @Parameter(hidden = true)
            CustomUserDetails userDetails,

            @Parameter(
                    description = "가게 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            UUID storeId
    );

    // 전체 조회
    @Operation(
            summary = "가게 목록 조회",
            description = """
                    가게 목록을 페이지 기반으로 조회합니다.
                    가게 이름과 카테고리/지역명을 이용해 검색할 수 있습니다.
                    """,
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "가게 목록 조회 성공"
                    )
            }
    )
    ResponseEntity<ApiResponse<StoreGetAllPageResponseDto>> getAllStores(
            @Parameter(
                    description = "가게 이름 검색 조건",
                    required = false,
                    example = "한솔 식당"
            )
            String name,

            @Parameter(
                    description = "카테고리 검색 조건",
                    required = false,
                    example = "한식"
            )
            String category,

            @Parameter(
                    description = "지역 검색 조건",
                    required = false,
                    example = "역삼동"
            )
            String area,

            @Parameter(
                    description = "페이지 조회 및 정렬 정보",
                    required = true
            )
            Pageable pageable
    );

    // 수정
    @Operation(
            summary = "가게 정보 수정",
            description = "가게 주인이나 관리자가 자신이 등록한 가게 정보를 수정합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "가게 정보 수정 성공"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "잘못된 가게 수정 정보",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "가게 수정 권한 없음",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "가게 또는 관련 정보를 찾을 수 없음",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "409",
                            description = "동일한 이름의 가게가 존재함",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<ApiResponse<StoreUpdateResponseDto>> updateStore(
            @Parameter(hidden = true)
            CustomUserDetails userDetails,

            @Parameter(
                    description = "가게 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            UUID storeId,

            @Parameter(
                    description = "가게 수정 정보",
                    required = true
            )
            StoreUpdateRequestDto requestDto
    );

    // 삭제
    @Operation(
            summary = "가게 삭제",
            description = "가게 주인이나 관리자가 자신이 등록한 가게를 삭제합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "204",
                            description = "가게 삭제 성공"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "가게 삭제 권한 없음",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "가게를 찾을 수 없음",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<Void> deleteStore(
            @Parameter(hidden = true)
            CustomUserDetails userDetails,

            @Parameter(
                    description = "가게 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            UUID storeId
    );
}
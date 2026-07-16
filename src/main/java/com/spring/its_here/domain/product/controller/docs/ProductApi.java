package com.spring.its_here.domain.product.controller.docs;

import com.spring.its_here.domain.product.dto.request.ProductCreateRequestDto;
import com.spring.its_here.domain.product.dto.request.ProductSearchCondition;
import com.spring.its_here.domain.product.dto.request.ProductUpdateRequestDto;
import com.spring.its_here.domain.product.dto.response.ProductCreateResponseDto;
import com.spring.its_here.domain.product.dto.response.ProductCursorResponseDto;
import com.spring.its_here.domain.product.dto.response.ProductResponseDto;
import com.spring.its_here.domain.product.dto.response.ProductUpdateResponseDto;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Tag(name = "Product", description = "상품 API")
public interface ProductApi {

    @Operation(
            summary = "상품 등록",
            description = "가게 소유자가 새로운 상품을 등록합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "201",
                            description = "상품 등록 성공"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "상품 등록 권한 없음",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "사용자 또는 가게를 찾을 수 없음",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<ApiResponse<ProductCreateResponseDto>> createProduct(
            @Parameter(hidden = true)
            CustomUserDetails userDetails,

            @Parameter(
                    description = "상품 등록 정보",
                    required = true
            )
            ProductCreateRequestDto request,

            @Parameter(
                    description = "상품 이미지",
                    required = false
            )
            MultipartFile image
    );

    @Operation(
            summary = "상품 수정",
            description = "가게 소유자가 자신이 소유한 가게의 상품 정보를 수정합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "상품 수정 성공"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "상품 수정 권한 없음",
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
    ResponseEntity<ApiResponse<ProductUpdateResponseDto>> updateProduct(
            @Parameter(hidden = true)
            CustomUserDetails userDetails,

            @Parameter(
                    description = "상품 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            UUID productId,

            @Parameter(
                    description = "상품 수정 정보",
                    required = true
            )
            ProductUpdateRequestDto request
    );

    @Operation(
            summary = "상품 삭제",
            description = "가게 소유자가 자신이 소유한 가게의 상품을 삭제합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "204",
                            description = "상품 삭제 성공"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "상품 삭제 권한 없음",
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
    ResponseEntity<Void> deleteProduct(
            @Parameter(hidden = true)
            CustomUserDetails userDetails,

            @Parameter(
                    description = "상품 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            UUID productId
    );

    @Operation(
            summary = "상품 단건 조회",
            description = "상품 ID를 이용해 상품 정보를 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "상품 조회 성공"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "상품을 찾을 수 없음",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<ApiResponse<ProductResponseDto>> getProduct(
            @Parameter(
                    description = "상품 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            UUID productId
    );

    @Operation(
            summary = "가게 상품 목록 조회",
            description = """
                    가게 ID를 이용해 해당 가게의 상품 목록을 커서 기반으로 조회합니다.
                    커서가 없는 경우 첫 페이지를 조회합니다.
                    """,
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "가게 상품 목록 조회 성공"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "잘못된 조회 조건",
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
    ResponseEntity<ApiResponse<ProductCursorResponseDto>> getStoreProducts(
            @ParameterObject
            ProductSearchCondition condition,

            @Parameter(
                    description = "가게 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            UUID storeId
    );
}
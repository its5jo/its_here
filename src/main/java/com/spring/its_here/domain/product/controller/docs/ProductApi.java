package com.spring.its_here.domain.product.controller.docs;

import com.spring.its_here.domain.product.dto.request.ProductCreateRequestDto;
import com.spring.its_here.domain.product.dto.response.ProductCreateResponseDto;
import com.spring.its_here.domain.product.dto.response.ProductResponseDto;
import com.spring.its_here.global.advice.ErrorResponse;
import com.spring.its_here.global.response.ApiResponse;
import com.spring.its_here.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
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
            summary = "상품 단건 조회",
            description = "상품 ID를 이용해 상품 정보를 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "상품 조회 성공",
                            content = @Content(
                                    schema = @Schema(implementation = ProductResponseDto.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "상품을 찾을 수 없음"
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
}
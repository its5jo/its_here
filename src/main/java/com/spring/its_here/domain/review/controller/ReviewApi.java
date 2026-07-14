package com.spring.its_here.domain.review.controller;


import com.spring.its_here.domain.review.dto.request.ReviewCreateRequestDto;
import com.spring.its_here.domain.review.dto.response.ReviewCreateResponseDto;
import com.spring.its_here.domain.review.dto.response.ReviewGetOneResponseDto;
import com.spring.its_here.global.advice.ErrorResponse;
import com.spring.its_here.global.response.ApiResponse;
import com.spring.its_here.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Review", description = "리뷰 API")
public interface ReviewApi {

    @Operation(
            summary = "리뷰 등록",
            description = "주문을 완료한 CUSTOMER가 본인의 주문에 리뷰를 등록합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "201",
                            description = "리뷰 등록 성공"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "주문 미완료 또는 유효하지 않은 리뷰 정보",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401",
                            description = "인증 실패",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "리뷰 등록 권한 없음",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "주문 또는 가게를 찾을 수 없음",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "409",
                            description = "해당 주문에 이미 리뷰가 존재함",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<ApiResponse<ReviewCreateResponseDto>> createReview(
            @Parameter(hidden = true)
            CustomUserDetails userDetails,

            @Parameter(
                    description = "리뷰 등록 정보",
                    required = true
            )
            ReviewCreateRequestDto request
    );

// TODO : 추후 주석 해제

//    @Operation(
//            summary = "리뷰 전체 조회",
//            description = "특정 가게에 작성된 리뷰 목록을 조회합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "리뷰 전체 조회 성공"
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "400",
//                            description = "유효하지 않은 평점 또는 조회 개수",
//                            content = @Content(
//                                    schema = @Schema(implementation = ErrorResponse.class)
//                            )
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "404",
//                            description = "가게를 찾을 수 없음",
//                            content = @Content(
//                                    schema = @Schema(implementation = ErrorResponse.class)
//                            )
//                    )
//            }
//    )
//    ResponseEntity<ApiResponse<ReviewGetAllResponseDto>> getAllReview(
//            @Parameter(description = "리뷰 검색 조건")
//            ReviewGetAllRequestDto request,
//
//            @Parameter(description = "페이지 번호, 조회 개수 및 정렬 조건")
//            Pageable pageable
//    );

//    @Operation(
//            summary = "리뷰 단건 조회",
//            description = "리뷰 ID로 삭제되지 않은 리뷰를 조회합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "리뷰 단건 조회 성공"
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "404",
//                            description = "리뷰를 찾을 수 없음",
//                            content = @Content(
//                                    schema = @Schema(implementation = ErrorResponse.class)
//                            )
//                    )
//            }
//    )
//    ResponseEntity<ApiResponse<ReviewGetOneResponseDto>> getOneReview(
//            @Parameter(
//                    description = "리뷰 ID",
//                    required = true
//            )
//            UUID reviewId
//    );

//    @Operation(
//            summary = "리뷰 수정",
//            description = "CUSTOMER가 본인이 작성한 리뷰를 수정합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "리뷰 수정 성공"
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "400",
//                            description = "유효하지 않은 평점 또는 리뷰 내용",
//                            content = @Content(
//                                    schema = @Schema(implementation = ErrorResponse.class)
//                            )
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "401",
//                            description = "인증 실패",
//                            content = @Content(
//                                    schema = @Schema(implementation = ErrorResponse.class)
//                            )
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "403",
//                            description = "리뷰 수정 권한 없음",
//                            content = @Content(
//                                    schema = @Schema(implementation = ErrorResponse.class)
//                            )
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "404",
//                            description = "리뷰를 찾을 수 없음",
//                            content = @Content(
//                                    schema = @Schema(implementation = ErrorResponse.class)
//                            )
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "409",
//                            description = "삭제된 리뷰이거나 수정 가능 기간이 만료됨",
//                            content = @Content(
//                                    schema = @Schema(implementation = ErrorResponse.class)
//                            )
//                    )
//            }
//    )
//    ResponseEntity<ApiResponse<ReviewUpdateResponseDto>> updateReview(
//            @Parameter(hidden = true)
//            CustomUserDetails userDetails,
//
//            @Parameter(
//                    description = "리뷰 ID",
//                    required = true
//            )
//            UUID reviewId,
//
//            @Parameter(
//                    description = "리뷰 수정 정보",
//                    required = true
//            )
//            ReviewUpdateRequestDto request
//    );

//    @Operation(
//            summary = "리뷰 삭제",
//            description = "CUSTOMER가 본인이 작성한 리뷰를 삭제합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "204",
//                            description = "리뷰 삭제 성공"
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "401",
//                            description = "인증 실패",
//                            content = @Content(
//                                    schema = @Schema(implementation = ErrorResponse.class)
//                            )
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "403",
//                            description = "리뷰 삭제 권한 없음",
//                            content = @Content(
//                                    schema = @Schema(implementation = ErrorResponse.class)
//                            )
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "404",
//                            description = "리뷰를 찾을 수 없음",
//                            content = @Content(
//                                    schema = @Schema(implementation = ErrorResponse.class)
//                            )
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "409",
//                            description = "이미 삭제된 리뷰",
//                            content = @Content(
//                                    schema = @Schema(implementation = ErrorResponse.class)
//                            )
//                    )
//            }
//    )
//    ResponseEntity<Void> deleteReview(
//            @Parameter(hidden = true)
//            CustomUserDetails userDetails,
//
//            @Parameter(
//                    description = "리뷰 ID",
//                    required = true
//            )
//            UUID reviewId
//    );
}

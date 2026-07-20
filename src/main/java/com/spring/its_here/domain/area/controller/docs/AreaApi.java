package com.spring.its_here.domain.area.controller.docs;

import com.spring.its_here.domain.area.dto.request.AreaCreateRequestDto;
import com.spring.its_here.domain.area.dto.request.AreaGetAllRequestDto;
import com.spring.its_here.domain.area.dto.request.AreaUpdateRequestDto;
import com.spring.its_here.domain.area.dto.response.AreaCreateResponseDto;
import com.spring.its_here.domain.area.dto.response.AreaGetAllResponseDto;
import com.spring.its_here.domain.area.dto.response.AreaGetOneResponseDto;
import com.spring.its_here.domain.area.dto.response.AreaUpdateResponseDto;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.UUID;

@Tag(name = "Area", description = "서비스 지역 API")
public interface AreaApi {

    @Operation(
            summary = "서비스 지역 등록",
            description = """
                    MASTER 또는 MANAGER 권한을 가진 사용자가 서비스 지역을 등록합니다.
                    city, district, town은 필수이며 동일한 지역은 중복 등록할 수 없습니다.
                    """,
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "201",
                            description = "서비스 지역 등록 성공"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "유효하지 않은 지역 정보",
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
                            description = "지역 등록 권한 없음",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "409",
                            description = "이미 등록된 지역",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<ApiResponse<AreaCreateResponseDto>> createArea(
            @Parameter(
                    description = "등록할 서비스 지역 정보",
                    required = true
            )
            AreaCreateRequestDto areaCreateRequestDto
    );

    @Operation(
            summary = "서비스 지역 단건 조회",
            description = "지역 ID를 이용하여 삭제되지 않은 서비스 지역을 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "서비스 지역 상세 조회 성공"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "유효하지 않은 지역 ID",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "서비스 지역을 찾을 수 없음",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<ApiResponse<AreaGetOneResponseDto>> getOneArea(
            @Parameter(
                    description = "서비스 지역 ID",
                    required = true
            )
            UUID areaId
    );

    @Operation(
            summary = "서비스 지역 전체 조회",
            description = """
                    등록된 서비스 지역 목록을 조회합니다.
                    city, district, town 및 서비스 가능 여부로 필터링할 수 있습니다.
                    삭제된 지역은 조회 결과에서 제외됩니다.
                    조회 개수는 10, 30, 50 중 하나를 사용할 수 있으며 기본값은 10입니다.
                    createdAt을 기준으로 정렬할 수 있습니다.
                    """,
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "서비스 지역 전체 조회 성공"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "유효하지 않은 조회 조건, 조회 개수 또는 정렬 기준",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<ApiResponse<AreaGetAllResponseDto>> getAllArea(
            @Parameter(description = "서비스 지역 검색 조건")
            AreaGetAllRequestDto areaGetAllRequestDto,

            @Parameter(
                    description = "페이지 번호, 조회 개수 및 정렬 조건"
            )
            Pageable pageable
    );

    @Operation(
            summary = "서비스 지역 수정",
            description = """
                    MASTER 또는 MANAGER 권한을 가진 사용자가 서비스 지역을 수정합니다.
                    존재하며 삭제되지 않은 지역만 수정할 수 있습니다.
                    """,
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "서비스 지역 수정 성공"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "유효하지 않은 요청값",
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
                            description = "지역 수정 권한 없음",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "서비스 지역을 찾을 수 없음",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "409",
                            description = "동일한 서비스 지역이 이미 존재함",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<ApiResponse<AreaUpdateResponseDto>> updateArea(
            @Parameter(
                    description = "수정할 서비스 지역 정보",
                    required = true
            )
            AreaUpdateRequestDto areaUpdateRequestDto,

            @Parameter(
                    description = "서비스 지역 ID",
                    required = true
            )
            UUID areaId
    );

    @Operation(
            summary = "서비스 지역 삭제",
            description = """
                    MASTER 또는 MANAGER 권한을 가진 사용자가 서비스 지역을 삭제합니다.
                    삭제는 soft delete 방식으로 처리되며, 삭제된 지역은 조회에서 제외됩니다.
                    """,
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "204",
                            description = "서비스 지역 삭제 성공"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "유효하지 않은 지역 ID",
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
                            description = "지역 삭제 권한 없음",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "서비스 지역을 찾을 수 없음",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "409",
                            description = "이미 삭제된 서비스 지역",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<Void> deleteArea(
            @AuthenticationPrincipal
            @Parameter(hidden = true)
            CustomUserDetails customUserDetails,

            @Parameter(
                    description = "서비스 지역 ID",
                    required = true
            )
            UUID areaId
    );
}
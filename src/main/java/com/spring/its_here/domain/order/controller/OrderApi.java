package com.spring.its_here.domain.order.controller;

import com.spring.its_here.domain.order.dto.request.OrderCreateRequestDto;
import com.spring.its_here.domain.order.dto.request.OrderStatusUpdateRequestDto;
import com.spring.its_here.domain.order.dto.response.OrderCancelResponseDto;
import com.spring.its_here.domain.order.dto.response.OrderListResponseDto;
import com.spring.its_here.domain.order.dto.response.OrderResponseDto;
import com.spring.its_here.domain.order.dto.response.OrderStatusResponseDto;
import com.spring.its_here.domain.order.enums.OrderStatus;
import com.spring.its_here.domain.payment.dto.response.PaymentResponseDto;
import com.spring.its_here.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import com.spring.its_here.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "주문 API", description = "주문 관련 API")
public interface OrderApi {
    @Operation(
            summary = "주문 생성",
            description = "로그인한 유저가 주문을 생성합니다. 주문 생성 시 결제도 함께 처리됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "주문 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 / 필수값 누락"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "로그인 필요"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "가게 또는 상품 없음")
    })
    ResponseEntity<ApiResponse<OrderResponseDto>> createOrder(
            @Parameter(hidden = true) CustomUserDetails userDetails,
            @Parameter(description = "주문 생성 요청") OrderCreateRequestDto request

    );

    @Operation(
            summary = "주문 단건 조회",
            description = "주문 ID로 단건 조회합니다. CUSTOMER는 본인 주문만, OWNER는 본인 가게 주문만 조회 가능합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "주문 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "로그인 필요"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "접근 권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "주문 없음")
    })
    ResponseEntity<ApiResponse<OrderResponseDto>> getOrder(
            @Parameter(hidden = true) CustomUserDetails userDetails,
            @Parameter(description = "주문 ID") UUID orderId
    );

    @Operation(
            summary = "주문 목록 조회",
            description = "주문 목록을 조회합니다. 권한에 따라 조회 범위가 다릅니다. 페이지 크기는 10, 30, 50만 허용합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "주문 목록 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "허용되지 않는 페이지 크기"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "로그인 필요")
    })
    ResponseEntity<ApiResponse<OrderListResponseDto>> getOrderList(
            @Parameter(description = "페이지 번호 (0부터)") int page,
            @Parameter(description = "페이지 크기 (10, 30, 50만 허용)") int size,
            @Parameter(description = "주문 상태 필터 (선택)") OrderStatus orderStatus,
            @Parameter(hidden = true) CustomUserDetails userDetails
    );

    @Operation(
            summary = "주문 취소",
            description = "주문 생성 후 5분 이내이면서 REQUESTED 상태인 주문만 취소 가능합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "주문 취소 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "취소 가능 시간 초과 또는 취소 불가 상태"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "본인의 주문이 아님"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "주문 없음")
    })
    ResponseEntity<ApiResponse<OrderCancelResponseDto>> cancelOrder(
            @Parameter(hidden = true) CustomUserDetails userDetails,
            @Parameter(description = "주문 ID") UUID orderId
    );

    @Operation(
            summary = "주문 상태 변경",
            description = "OWNER는 본인 가게 주문만, MANAGER/MASTER는 전체 변경 가능합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "주문 상태 변경 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효하지 않은 상태 전이"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "주문 없음")
    })
    ResponseEntity<ApiResponse<OrderStatusResponseDto>> updateOrderStatus(
            @Parameter(hidden = true) CustomUserDetails userDetails,
            @Parameter(description = "주문 ID") UUID orderId,
            @Parameter(description = "변경할 주문 상태") OrderStatusUpdateRequestDto requestDto
    );

    @Operation(
            summary = "주문별 결제 조회",
            description = "주문 ID로 해당 주문의 결제 정보를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "결제 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "로그인 필요"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "결제 없음")
    })
    ResponseEntity<ApiResponse<PaymentResponseDto>> getPayment(
            @Parameter(hidden = true) CustomUserDetails userDetails,
            @Parameter(description = "주문 ID") UUID orderId
    );
}

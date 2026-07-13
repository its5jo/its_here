package com.spring.its_here.domain.order.controller;

import com.spring.its_here.domain.order.dto.request.OrderCreateRequestDto;
import com.spring.its_here.domain.order.dto.response.OrderResponseDto;
import com.spring.its_here.domain.order.dto.response.OrderSummaryResponseDto;
import com.spring.its_here.domain.order.enums.OrderStatus;
import com.spring.its_here.domain.order.service.OrderService;
import com.spring.its_here.global.response.ApiResponse;
import com.spring.its_here.global.response.PageResponse;
import com.spring.its_here.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.DialectOverride;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/orders")
@RestController
@RequiredArgsConstructor
public class OrderController implements OrderApi {

    private final OrderService orderService;

    @PostMapping
    @Override
    public ResponseEntity<ApiResponse<OrderResponseDto>> createOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody OrderCreateRequestDto requestDto) {
        OrderResponseDto responseDto = orderService.create(requestDto, userDetails.getUserId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("주문 생성 성공", responseDto));
    }

    @GetMapping("/{orderId}")
    @Override
    public ResponseEntity<ApiResponse<OrderResponseDto>> getOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID orderId) {
        OrderResponseDto responseDto = orderService.getOrder(orderId, userDetails.getUserId(),userDetails.getRole());
        return ResponseEntity.ok(ApiResponse.success("주문 조회 성공", responseDto));
    }

    @GetMapping
    @Override
    public ResponseEntity<ApiResponse<PageResponse<OrderSummaryResponseDto>>> getOrderList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false)OrderStatus orderStatus,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ) {
        PageResponse<OrderSummaryResponseDto> response = orderService.getOrderList(
                page, size, userDetails.getUserId(), userDetails.getRole(), orderStatus
        );
        return ResponseEntity.ok(ApiResponse.success("주문 목록 조회 성공", response));
    }
}
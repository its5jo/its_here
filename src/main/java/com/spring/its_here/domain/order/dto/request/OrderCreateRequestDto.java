package com.spring.its_here.domain.order.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record OrderCreateRequestDto(
        @NotNull(message = "가게 ID는 필수입니다")
        UUID storeId,

        @NotEmpty(message = "주문 상품은 1개 이상이어야 합니다")
        @Valid
        List<OrderProductRequestDto> orderProducts,

        @NotBlank(message = "배송지 입력은 필수입니다")
        String deliveryAddress,

        String requestMemo,

        @NotBlank(message = "결제 수단은 필수입니다")
        String paymentMethod
) { }

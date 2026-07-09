package com.spring.its_here.domain.order.dto.request;

public record OrderUpdateRequest(

        String deliveryAddress,

        String requestMemo
) {
}

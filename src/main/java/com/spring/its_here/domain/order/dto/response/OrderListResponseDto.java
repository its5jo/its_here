package com.spring.its_here.domain.order.dto.response;

import com.spring.its_here.domain.order.entity.Order;
import com.spring.its_here.global.response.OffsetPageInfo;
import org.springframework.data.domain.Page;

import java.util.List;

public record OrderListResponseDto(
        List<OrderSummaryResponseDto> content,
        OffsetPageInfo pageInfo
) {
    public static OrderListResponseDto from(Page<Order> page) {
        return new OrderListResponseDto(
                page.getContent().stream()
                        .map(OrderSummaryResponseDto::from)
                        .toList(),
                OffsetPageInfo.from(page)
        );
    }
}
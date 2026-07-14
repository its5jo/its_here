package com.spring.its_here.domain.order.dto.response;

import com.spring.its_here.domain.order.entity.Order;
import com.spring.its_here.domain.order.enums.OrderStatus;
import com.spring.its_here.domain.payment.entity.Payment;
import com.spring.its_here.domain.payment.enums.PaymentStatus;

import java.util.UUID;

public record OrderCancelResponseDto(
        UUID orderId,
        OrderStatus status,
        PaymentInfo payment
) {
    public record PaymentInfo (
            UUID paymentId,
            PaymentStatus status
    ) {
        public static PaymentInfo from(Payment payment) {
            return new PaymentInfo(payment.getId(), payment.getStatus());
        }
    }
    public static OrderCancelResponseDto from(Order order, Payment payment) {
        return new OrderCancelResponseDto(
                order.getId(),
                order.getStatus(),
                PaymentInfo.from(payment)
        );
    }
}

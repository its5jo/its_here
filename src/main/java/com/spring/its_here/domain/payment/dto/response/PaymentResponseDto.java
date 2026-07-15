package com.spring.its_here.domain.payment.dto.response;

import com.spring.its_here.domain.payment.entity.Payment;
import com.spring.its_here.domain.payment.enums.PaymentMethod;
import com.spring.its_here.domain.payment.enums.PaymentStatus;

import java.time.Instant;
import java.util.UUID;

public record PaymentResponseDto(
        UUID paymentId,
        UUID orderId,
        int amount,
        int originalAmount,
        PaymentMethod method,
        PaymentStatus status,
        Instant approvedAt
) {
    public static PaymentResponseDto from(Payment payment) {
        return new PaymentResponseDto(
                payment.getId(),
                payment.getOrderId(),
                payment.getAmount(),
                payment.getOriginalAmount(),
                payment.getMethod(),
                payment.getStatus(),
                payment.getApprovedAt()
        );
    }
}

package com.spring.its_here.domain.payment.entity;

import com.spring.its_here.domain.payment.enums.PaymentMethod;
import com.spring.its_here.domain.payment.enums.PaymentStatus;
import com.spring.its_here.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "p_payment")
@Getter
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "amount", nullable = false)
    private int amount;

    @Column(name = "original_amount", nullable = false)
    private int originalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false)
    private PaymentMethod method;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    public static Payment createForOrder(UUID orderId, int amount, PaymentMethod method) {
        Payment payment = new Payment();
        payment.orderId = orderId;
        payment.amount = amount;
        payment.originalAmount = amount;   // 할인 없으니 동일
        payment.method = method;
        payment.status = PaymentStatus.COMPLETED;
        payment.approvedAt = Instant.now();  // PG 없으니 생성 시각 = 승인 시각
        return payment;
    }

}

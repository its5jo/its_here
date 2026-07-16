package com.spring.its_here.domain.payment.service;

import com.spring.its_here.domain.payment.dto.response.PaymentResponseDto;
import com.spring.its_here.domain.payment.entity.Payment;
import com.spring.its_here.domain.payment.enums.PaymentMethod;
import com.spring.its_here.domain.payment.repository.PaymentRepository;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentResponseDto createForOrder(UUID orderId, int totalAmount)
    {
        Payment payment = Payment.createForOrder(orderId, totalAmount, PaymentMethod.CARD);
        return PaymentResponseDto.from(paymentRepository.save(payment));
    }

    //주문 별 결제 조회
    @Transactional(readOnly = true)
    public PaymentResponseDto getPaymentByOrderId(UUID orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ItsHereException(ErrorCode.PAYMENT_NOT_FOUND));
        return PaymentResponseDto.from(payment);
    }

    public Payment cancelPayment(UUID orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ItsHereException(ErrorCode.PAYMENT_NOT_FOUND));
        payment.cancel();
        return payment;
    }
}

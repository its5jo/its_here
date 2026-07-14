package com.spring.its_here.domain.payment.service;

import com.spring.its_here.domain.payment.dto.response.PaymentResponseDto;
import com.spring.its_here.domain.payment.entity.Payment;
import com.spring.its_here.domain.payment.enums.PaymentMethod;
import com.spring.its_here.domain.payment.repository.PaymentRepository;
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

    public PaymentResponseDto getPayment(UUID orderId) {
        return null;  // TODO
    }

    //주문 별 결제 조회
    public PaymentResponseDto getPaymentByOrderId(UUID orderId) {
        return null;  // TODO
    }
}

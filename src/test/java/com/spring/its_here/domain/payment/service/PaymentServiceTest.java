package com.spring.its_here.domain.payment.service;

import com.spring.its_here.domain.payment.dto.response.PaymentResponseDto;
import com.spring.its_here.domain.payment.entity.Payment;
import com.spring.its_here.domain.payment.enums.PaymentMethod;
import com.spring.its_here.domain.payment.enums.PaymentStatus;
import com.spring.its_here.domain.payment.repository.PaymentRepository;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    private static final UUID ORDER_ID = UUID.randomUUID();
    private static final UUID PAYMENT_ID = UUID.randomUUID();

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = Payment.createForOrder(ORDER_ID, 20000, PaymentMethod.CARD);
        ReflectionTestUtils.setField(payment, "id", PAYMENT_ID);
    }

    @Nested
    @DisplayName("주문별 결제 조회 테스트")
    class GetPaymentByOrderIdTest {

        @Test
        @DisplayName("주문별 결제 조회 성공")
        void getPaymentByOrderId_success() {
            // given
            when(paymentRepository.findByOrderId(ORDER_ID))
                    .thenReturn(Optional.of(payment));

            // when
            PaymentResponseDto response = paymentService.getPaymentByOrderId(ORDER_ID);

            // then
            assertThat(response.paymentId()).isEqualTo(PAYMENT_ID);
            assertThat(response.orderId()).isEqualTo(ORDER_ID);
            assertThat(response.amount()).isEqualTo(20000);
            assertThat(response.status()).isEqualTo(PaymentStatus.COMPLETED);
            assertThat(response.method()).isEqualTo(PaymentMethod.CARD);
            verify(paymentRepository).findByOrderId(ORDER_ID);
        }

        @Test
        @DisplayName("주문별 결제 조회 실패 - 결제 없음")
        void getPaymentByOrderId_fail_notFound() {
            // given
            when(paymentRepository.findByOrderId(ORDER_ID))
                    .thenReturn(Optional.empty());

            // when
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> paymentService.getPaymentByOrderId(ORDER_ID)
            );

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PAYMENT_NOT_FOUND);
            verify(paymentRepository).findByOrderId(ORDER_ID);
        }
    }
    @Nested
    @DisplayName("결제 생성 테스트")
    class CreateForOrderTest {

        @Test
        @DisplayName("결제 생성 성공")
        void createForOrder_success() {
            // given
            when(paymentRepository.save(any(Payment.class)))
                    .thenAnswer(invocation -> {
                        Payment p = invocation.getArgument(0);
                        ReflectionTestUtils.setField(p, "id", PAYMENT_ID);
                        return p;
                    });

            // when
            PaymentResponseDto response = paymentService.createForOrder(ORDER_ID, 20000);

            // then
            assertThat(response.orderId()).isEqualTo(ORDER_ID);
            assertThat(response.amount()).isEqualTo(20000);
            assertThat(response.originalAmount()).isEqualTo(20000);
            assertThat(response.status()).isEqualTo(PaymentStatus.COMPLETED);
            assertThat(response.method()).isEqualTo(PaymentMethod.CARD);
            assertThat(response.approvedAt()).isNotNull();
            verify(paymentRepository).save(any(Payment.class));
        }
    }

    @Nested
    @DisplayName("결제 취소 테스트")
    class CancelPaymentTest {

        @Test
        @DisplayName("결제 취소 성공")
        void cancelPayment_success() {
            // given
            when(paymentRepository.findByOrderId(ORDER_ID))
                    .thenReturn(Optional.of(payment));

            // when
            Payment result = paymentService.cancelPayment(ORDER_ID);

            // then
            assertThat(result.getStatus()).isEqualTo(PaymentStatus.CANCELED);
            verify(paymentRepository).findByOrderId(ORDER_ID);
        }

        @Test
        @DisplayName("결제 취소 실패 - 결제 없음")
        void cancelPayment_fail_notFound() {
            // given
            when(paymentRepository.findByOrderId(ORDER_ID))
                    .thenReturn(Optional.empty());

            // when
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> paymentService.cancelPayment(ORDER_ID)
            );

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PAYMENT_NOT_FOUND);
        }
    }
}


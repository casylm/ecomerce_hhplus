package me.seyoung.ecomerce.application.payment;

import me.seyoung.ecomerce.domain.payment.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CanclePaymentUseCase 단위 테스트")
class CanclePaymentUseCaseTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private CanclePaymentUseCase canclePaymentUseCase;

    @Test
    @DisplayName("성공한 결제를 취소한다")
    void 성공한_결제를_취소한다() {
        // given
        Long paymentId = 1L;
        Long orderId = 100L;
        Long amount = 50000L;

        Payment payment = Payment.create(orderId, new Price(amount));
        payment.assignId(paymentId);
        payment.complete(); // SUCCESS 상태로 변경

        given(paymentRepository.findById(paymentId)).willReturn(Optional.of(payment));

        Payment cancelledPayment = Payment.create(orderId, new Price(amount));
        cancelledPayment.assignId(paymentId);
        cancelledPayment.complete();
        cancelledPayment.cancel();

        given(paymentRepository.save(any(Payment.class))).willReturn(cancelledPayment);

        // when
        PaymentInfo.Result result = canclePaymentUseCase.execute(paymentId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.paymentId()).isEqualTo(paymentId);
        assertThat(result.orderId()).isEqualTo(orderId);
        assertThat(result.status()).isEqualTo(PaymentStatus.CANCELLED);
        assertThat(result.cancelledAt()).isNotNull();

        verify(paymentRepository, times(1)).findById(paymentId);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("존재하지 않는 결제를 취소하면 예외가 발생한다")
    void 존재하지_않는_결제를_취소하면_예외가_발생한다() {
        // given
        Long paymentId = 999L;
        given(paymentRepository.findById(paymentId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> canclePaymentUseCase.execute(paymentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결제 정보를 찾을 수 없습니다. paymentId=" + paymentId);

        verify(paymentRepository, times(1)).findById(paymentId);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("PENDING 상태의 결제는 취소할 수 없다")
    void PENDING_상태의_결제는_취소할_수_없다() {
        // given
        Long paymentId = 1L;
        Long orderId = 100L;
        Long amount = 50000L;

        Payment payment = Payment.create(orderId, new Price(amount));
        payment.assignId(paymentId);
        // PENDING 상태 유지

        given(paymentRepository.findById(paymentId)).willReturn(Optional.of(payment));

        // when & then
        assertThatThrownBy(() -> canclePaymentUseCase.execute(paymentId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("성공한 결제만 취소할 수 있습니다.");

        verify(paymentRepository, times(1)).findById(paymentId);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("FAILED 상태의 결제는 취소할 수 없다")
    void FAILED_상태의_결제는_취소할_수_없다() {
        // given
        Long paymentId = 1L;
        Long orderId = 100L;
        Long amount = 50000L;

        Payment payment = Payment.create(orderId, new Price(amount));
        payment.assignId(paymentId);
        payment.fail(); // FAILED 상태로 변경

        given(paymentRepository.findById(paymentId)).willReturn(Optional.of(payment));

        // when & then
        assertThatThrownBy(() -> canclePaymentUseCase.execute(paymentId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("성공한 결제만 취소할 수 있습니다.");

        verify(paymentRepository, times(1)).findById(paymentId);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("결제 취소 시 Payment 객체가 올바르게 변경된다")
    void 결제_취소_시_Payment_객체가_올바르게_변경된다() {
        // given
        Long paymentId = 1L;
        Long orderId = 100L;
        Long amount = 50000L;

        Payment payment = Payment.create(orderId, new Price(amount));
        payment.assignId(paymentId);
        payment.complete();

        given(paymentRepository.findById(paymentId)).willReturn(Optional.of(payment));

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);

        Payment cancelledPayment = Payment.create(orderId, new Price(amount));
        cancelledPayment.assignId(paymentId);
        cancelledPayment.complete();
        cancelledPayment.cancel();

        given(paymentRepository.save(any(Payment.class))).willReturn(cancelledPayment);

        // when
        canclePaymentUseCase.execute(paymentId);

        // then
        verify(paymentRepository).save(paymentCaptor.capture());
        Payment capturedPayment = paymentCaptor.getValue();

        assertThat(capturedPayment.getStatus()).isEqualTo(PaymentStatus.CANCELLED);
        assertThat(capturedPayment.getCancelledAt()).isNotNull();
    }
}

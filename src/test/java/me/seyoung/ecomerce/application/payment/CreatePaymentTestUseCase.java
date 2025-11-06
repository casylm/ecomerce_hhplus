package me.seyoung.ecomerce.application.payment;

import me.seyoung.ecomerce.domain.payment.*;
import me.seyoung.ecomerce.infrastructure.payment.InMemoryPaymentRepository;
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
@DisplayName("CreatePaymentUseCase 단위 테스트")
class CreatePaymentTestUseCase {

    @Mock
    private InMemoryPaymentRepository paymentRepository;

    @InjectMocks
    private CreatePaymentUseCase createPaymentUseCase;

    @Test
    @DisplayName("결제를 성공적으로 생성한다")
    void 결제를_성공적으로_생성한다() {
        // given
        Long orderId = 1L;
        Long amount = 50000L;
        Long userId = 100L;
        Pay command = new Pay(orderId, amount, userId, null);

        given(paymentRepository.findByOrderId(orderId)).willReturn(Optional.empty());

        Payment savedPayment = Payment.create(orderId, new Price(amount));
        savedPayment.assignId(1L);
        savedPayment.complete();

        given(paymentRepository.save(any(Payment.class))).willReturn(savedPayment);

        // when
        PaymentInfo.Result result = createPaymentUseCase.execute(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.orderId()).isEqualTo(orderId);
        assertThat(result.amount()).isEqualTo(amount);
        assertThat(result.status()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(result.paidAt()).isNotNull();

        verify(paymentRepository, times(1)).findByOrderId(orderId);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("이미 결제된 주문이면 예외가 발생한다")
    void 이미_결제된_주문이면_예외가_발생한다() {
        // given
        Long orderId = 1L;
        Long amount = 50000L;
        Pay command = new Pay(orderId, amount, 100L, null);

        Payment existingPayment = Payment.create(orderId, new Price(amount));
        existingPayment.complete();

        given(paymentRepository.findByOrderId(orderId)).willReturn(Optional.of(existingPayment));

        // when & then
        assertThatThrownBy(() -> createPaymentUseCase.execute(command))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 결제된 주문입니다.");

        verify(paymentRepository, times(1)).findByOrderId(orderId);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("결제가 PENDING 상태였던 주문은 다시 결제할 수 있다")
    void 결제가_PENDING_상태였던_주문은_다시_결제할_수_있다() {
        // given
        Long orderId = 1L;
        Long amount = 50000L;
        Pay command = new Pay(orderId, amount, 100L, null);

        Payment existingPayment = Payment.create(orderId, new Price(amount));
        // PENDING 상태 유지 (complete() 호출 안 함)

        given(paymentRepository.findByOrderId(orderId)).willReturn(Optional.of(existingPayment));

        Payment savedPayment = Payment.create(orderId, new Price(amount));
        savedPayment.assignId(1L);
        savedPayment.complete();

        given(paymentRepository.save(any(Payment.class))).willReturn(savedPayment);

        // when
        PaymentInfo.Result result = createPaymentUseCase.execute(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(PaymentStatus.SUCCESS);

        verify(paymentRepository, times(1)).findByOrderId(orderId);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("결제가 FAILED 상태였던 주문은 다시 결제할 수 있다")
    void 결제가_FAILED_상태였던_주문은_다시_결제할_수_있다() {
        // given
        Long orderId = 1L;
        Long amount = 50000L;
        Pay command = new Pay(orderId, amount, 100L, null);

        Payment existingPayment = Payment.create(orderId, new Price(amount));
        existingPayment.fail(); // FAILED 상태로 변경

        given(paymentRepository.findByOrderId(orderId)).willReturn(Optional.of(existingPayment));

        Payment savedPayment = Payment.create(orderId, new Price(amount));
        savedPayment.assignId(1L);
        savedPayment.complete();

        given(paymentRepository.save(any(Payment.class))).willReturn(savedPayment);

        // when
        PaymentInfo.Result result = createPaymentUseCase.execute(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(PaymentStatus.SUCCESS);

        verify(paymentRepository, times(1)).findByOrderId(orderId);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Payment 객체가 올바르게 생성되고 complete 상태가 된다")
    void Payment_객체가_올바르게_생성되고_complete_상태가_된다() {
        // given
        Long orderId = 1L;
        Long amount = 50000L;
        Pay command = new Pay(orderId, amount, 100L, null);

        given(paymentRepository.findByOrderId(orderId)).willReturn(Optional.empty());

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);

        Payment savedPayment = Payment.create(orderId, new Price(amount));
        savedPayment.assignId(1L);
        savedPayment.complete();

        given(paymentRepository.save(any(Payment.class))).willReturn(savedPayment);

        // when
        createPaymentUseCase.execute(command);

        // then
        verify(paymentRepository).save(paymentCaptor.capture());
        Payment capturedPayment = paymentCaptor.getValue();

        assertThat(capturedPayment.getOrderId()).isEqualTo(orderId);
        assertThat(capturedPayment.getAmount().getValue()).isEqualTo(amount);
        assertThat(capturedPayment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
    }
}

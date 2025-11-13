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

    @Mock
    private me.seyoung.ecomerce.domain.order.OrderRepository orderRepository;

    @Mock
    private me.seyoung.ecomerce.domain.product.ProductRepository productRepository;

    @Mock
    private me.seyoung.ecomerce.domain.point.PointRepository pointRepository;

    @Mock
    private me.seyoung.ecomerce.domain.point.PointHistoryRepository pointHistoryRepository;

    @Mock
    private me.seyoung.ecomerce.domain.coupon.UserCouponRepository userCouponRepository;

    @InjectMocks
    private CanclePaymentUseCase canclePaymentUseCase;

    @Test
    @DisplayName("성공한 결제를 취소한다")
    void 성공한_결제를_취소한다() {
        // given
        Long paymentId = 1L;
        Long orderId = 100L;
        Long amount = 50000L;

        Payment payment = Payment.create(orderId, new Price(amount), null, 0L);
        payment.assignId(paymentId);
        payment.complete(); // SUCCESS 상태로 변경

        given(paymentRepository.findById(paymentId)).willReturn(Optional.of(payment));
        given(orderRepository.findById(orderId)).willReturn(Optional.of(
            me.seyoung.ecomerce.domain.order.Order.create(1L, java.util.Collections.emptyList(), new Price(amount))
        ));

        Payment cancelledPayment = Payment.create(orderId, new Price(amount), null, 0L);
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

        Payment payment = Payment.create(orderId, new Price(amount), null, 0L);
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

        Payment payment = Payment.create(orderId, new Price(amount), null, 0L);
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
}

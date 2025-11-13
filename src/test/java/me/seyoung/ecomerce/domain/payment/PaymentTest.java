package me.seyoung.ecomerce.domain.payment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Payment 도메인 테스트")
class PaymentTest {

    @Test
    @DisplayName("Payment 생성 시 PENDING 상태로 초기화된다")
    void Payment_생성_시_PENDING_상태() {
        // given
        Long orderId = 1L;
        Price amount = new Price(50000L);

        // when
        Payment payment = Payment.create(orderId, amount, null, 0L);

        // then
        assertThat(payment.getOrderId()).isEqualTo(orderId);
        assertThat(payment.getAmount()).isEqualTo(amount);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(payment.getPaidAt()).isNull();
        assertThat(payment.getCancelledAt()).isNull();
    }

    @Test
    @DisplayName("Payment 완료 시 SUCCESS 상태로 변경되고 paidAt이 설정된다")
    void Payment_완료_시_SUCCESS_상태() {
        // given
        Payment payment = Payment.create(1L, new Price(50000L), null, 0L);

        // when
        payment.complete();

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(payment.getPaidAt()).isNotNull();
    }

    @Test
    @DisplayName("이미 완료된 Payment를 다시 완료하면 예외가 발생한다")
    void 이미_완료된_Payment_재완료_시_예외() {
        // given
        Payment payment = Payment.create(1L, new Price(50000L), null, 0L);
        payment.complete();

        // when & then
        assertThatThrownBy(() -> payment.complete())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 결제된 주문입니다.");
    }

    @Test
    @DisplayName("PENDING 상태의 Payment를 실패 처리하면 FAILED 상태로 변경된다")
    void PENDING_상태_Payment_실패_처리() {
        // given
        Payment payment = Payment.create(1L, new Price(50000L), null, 0L);

        // when
        payment.fail();

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
    }

    @Test
    @DisplayName("SUCCESS 상태의 Payment를 실패 처리하면 예외가 발생한다")
    void SUCCESS_상태_Payment_실패_처리_시_예외() {
        // given
        Payment payment = Payment.create(1L, new Price(50000L), null, 0L);
        payment.complete();

        // when & then
        assertThatThrownBy(() -> payment.fail())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 결제된 주문은 실패 처리할 수 없습니다.");
    }

    @Test
    @DisplayName("SUCCESS 상태의 Payment를 취소하면 CANCELLED 상태로 변경되고 cancelledAt이 설정된다")
    void SUCCESS_상태_Payment_취소() {
        // given
        Payment payment = Payment.create(1L, new Price(50000L), null, 0L);
        payment.complete();

        // when
        payment.cancel();

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCELLED);
        assertThat(payment.getCancelledAt()).isNotNull();
    }

    @Test
    @DisplayName("PENDING 상태의 Payment를 취소하면 예외가 발생한다")
    void PENDING_상태_Payment_취소_시_예외() {
        // given
        Payment payment = Payment.create(1L, new Price(50000L), null, 0L);

        // when & then
        assertThatThrownBy(() -> payment.cancel())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("성공한 결제만 취소할 수 있습니다.");
    }

    @Test
    @DisplayName("FAILED 상태의 Payment를 취소하면 예외가 발생한다")
    void FAILED_상태_Payment_취소_시_예외() {
        // given
        Payment payment = Payment.create(1L, new Price(50000L), null, 0L);
        payment.fail();

        // when & then
        assertThatThrownBy(() -> payment.cancel())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("성공한 결제만 취소할 수 있습니다.");
    }

    @Test
    @DisplayName("CANCELLED 상태의 Payment를 다시 취소하면 예외가 발생한다")
    void CANCELLED_상태_Payment_재취소_시_예외() {
        // given
        Payment payment = Payment.create(1L, new Price(50000L), null, 0L);
        payment.complete();
        payment.cancel();

        // when & then
        assertThatThrownBy(() -> payment.cancel())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("성공한 결제만 취소할 수 있습니다.");
    }

    @Test
    @DisplayName("Payment에 ID를 할당할 수 있다")
    void Payment_ID_할당() {
        // given
        Payment payment = Payment.create(1L, new Price(50000L), null, 0L);
        Long assignedId = 100L;

        // when
        payment.assignId(assignedId);

        // then
        assertThat(payment.getId()).isEqualTo(assignedId);
    }

    @Test
    @DisplayName("결제 금액이 올바르게 저장된다")
    void 결제_금액_확인() {
        // given
        Long orderId = 1L;
        Price amount = new Price(123456L);

        // when
        Payment payment = Payment.create(orderId, amount, null, 0L);

        // then
        assertThat(payment.getAmount()).isEqualTo(123456L);
    }

    @Test
    @DisplayName("주문 ID가 올바르게 저장된다")
    void 주문_ID_확인() {
        // given
        Long orderId = 999L;
        Price amount = new Price(50000L);

        // when
        Payment payment = Payment.create(orderId, amount, null, 0L);

        // then
        assertThat(payment.getOrderId()).isEqualTo(orderId);
    }

    @Test
    @DisplayName("결제 상태 전환 시나리오: PENDING -> SUCCESS -> CANCELLED")
    void 결제_상태_전환_시나리오() {
        // given
        Payment payment = Payment.create(1L, new Price(50000L), null, 0L);

        // when & then - PENDING
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);

        // when & then - SUCCESS
        payment.complete();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(payment.getPaidAt()).isNotNull();

        // when & then - CANCELLED
        payment.cancel();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCELLED);
        assertThat(payment.getCancelledAt()).isNotNull();
    }

    @Test
    @DisplayName("결제 실패 시나리오: PENDING -> FAILED")
    void 결제_실패_시나리오() {
        // given
        Payment payment = Payment.create(1L, new Price(50000L), null, 0L);

        // when
        payment.fail();

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
        assertThat(payment.getPaidAt()).isNull();
        assertThat(payment.getCancelledAt()).isNull();
    }
}

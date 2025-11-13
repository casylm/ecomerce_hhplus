package me.seyoung.ecomerce.application.payment;

import me.seyoung.ecomerce.application.coupon.ApplyCouponUseCase;
import me.seyoung.ecomerce.domain.order.Order;
import me.seyoung.ecomerce.domain.order.OrderItem;
import me.seyoung.ecomerce.domain.order.OrderRepository;
import me.seyoung.ecomerce.domain.payment.*;
import me.seyoung.ecomerce.domain.point.PointRepository;
import me.seyoung.ecomerce.domain.product.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreatePaymentUseCase 단위 테스트")
class CreatePaymentTestUseCase {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ApplyCouponUseCase applyCouponUseCase;

    @Mock
    private PointRepository pointRepository;

    @InjectMocks
    private CreatePaymentUseCase createPaymentUseCase;

    @Test
    @DisplayName("결제를 성공적으로 생성한다")
    void 결제를_성공적으로_생성한다() {
        // given
        Long orderId = 1L;
        Long userId = 100L;
        Pay command = new Pay(orderId, 20000L, userId, null, null);

        // Order Mock 설정 (총액 20000L)
        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem orderItem = OrderItem.create(1L, 2, 10000L);
        orderItems.add(orderItem);

        Order order = Order.create(userId, orderItems, new Price(20000L));
        order.assignId(orderId);

        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

        Payment savedPayment = Payment.create(orderId, new Price(20000L), null, 0L);
        savedPayment.assignId(1L);
        savedPayment.complete();

        given(paymentRepository.save(any(Payment.class))).willReturn(savedPayment);

        // when
        PaymentInfo.Result result = createPaymentUseCase.execute(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.orderId()).isEqualTo(orderId);
        assertThat(result.amount()).isEqualTo(20000L);
        assertThat(result.status()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(result.paidAt()).isNotNull();

        verify(orderRepository, times(1)).findById(orderId);
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(productRepository, times(1)).deductStock(1L, 2);
    }

    @Test
    @DisplayName("주문을 찾을 수 없으면 예외가 발생한다")
    void 주문을_찾을_수_없으면_예외가_발생한다() {
        // given
        Long orderId = 1L;
        Long userId = 100L;
        Pay command = new Pay(orderId, 50000L, userId, null, null);

        given(orderRepository.findById(orderId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> createPaymentUseCase.execute(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 주문입니다. orderId=" + orderId);

        verify(orderRepository, times(1)).findById(orderId);
        verify(paymentRepository, never()).save(any(Payment.class));
    }
}

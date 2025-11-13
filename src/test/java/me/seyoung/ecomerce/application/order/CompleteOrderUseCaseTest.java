package me.seyoung.ecomerce.application.order;

import me.seyoung.ecomerce.domain.order.Order;
import me.seyoung.ecomerce.domain.order.OrderItem;
import me.seyoung.ecomerce.domain.order.OrderStatus;
import me.seyoung.ecomerce.domain.payment.Price;
import me.seyoung.ecomerce.infrastructure.order.InMemoryOrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("CompleteOrderUseCase 단위 테스트")
class CompleteOrderUseCaseTest {

    @Mock
    private InMemoryOrderRepository orderRepository;

    @InjectMocks
    private CompleteOrderUseCase completeOrderUseCase;

    @Test
    @DisplayName("결제 완료된 주문을 완료 처리할 수 있다")
    void 결제_완료된_주문_완료_처리() {
        // given
        Long orderId = 1L;
        Long userId = 100L;

        List<OrderItem> items = List.of(OrderItem.create(200L, 2, 10000L));
        Order order = Order.create(userId, items, new Price(20000L));
        order.assignId(orderId);
        order.markAsPaid(); // 결제 완료 상태로 변경

        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
        given(orderRepository.save(any(Order.class))).willReturn(order);

        // when
        completeOrderUseCase.execute(orderId);

        // then
        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(order);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("존재하지 않는 주문을 완료 처리하면 예외가 발생한다")
    void 존재하지_않는_주문_완료_처리_시_예외() {
        // given
        Long orderId = 999L;
        given(orderRepository.findById(orderId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> completeOrderUseCase.execute(orderId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 주문입니다. orderId=" + orderId);
    }

    @Test
    @DisplayName("결제 완료되지 않은 주문을 완료 처리하면 예외가 발생한다")
    void 결제_미완료_주문_완료_처리_시_예외() {
        // given
        Long orderId = 1L;
        Long userId = 100L;

        List<OrderItem> items = List.of(OrderItem.create(200L, 1, 10000L));
        Order order = Order.create(userId, items, new Price(10000L));
        order.assignId(orderId);
        // 결제 완료 처리를 하지 않음 (CREATED 상태)

        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> completeOrderUseCase.execute(orderId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("결제 완료된 주문만 완료 처리할 수 있습니다.");
    }

    @Test
    @DisplayName("이미 완료된 주문을 다시 완료 처리하면 예외가 발생한다")
    void 이미_완료된_주문_재완료_처리_시_예외() {
        // given
        Long orderId = 1L;
        Long userId = 100L;

        List<OrderItem> items = List.of(OrderItem.create(200L, 1, 10000L));
        Order order = Order.create(userId, items, new Price(10000L));
        order.assignId(orderId);
        order.markAsPaid();
        order.complete(); // 이미 완료 처리됨

        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> completeOrderUseCase.execute(orderId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 완료된 주문입니다.");
    }

    @Test
    @DisplayName("취소된 주문을 완료 처리하면 예외가 발생한다")
    void 취소된_주문_완료_처리_시_예외() {
        // given
        Long orderId = 1L;
        Long userId = 100L;

        List<OrderItem> items = List.of(OrderItem.create(200L, 1, 10000L));
        Order order = Order.create(userId, items, new Price(10000L));
        order.assignId(orderId);
        order.cancel(); // 주문 취소됨

        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> completeOrderUseCase.execute(orderId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("결제 완료된 주문만 완료 처리할 수 있습니다.");
    }

    @Test
    @DisplayName("여러 상품이 포함된 주문을 완료 처리할 수 있다")
    void 여러_상품_주문_완료_처리() {
        // given
        Long orderId = 1L;
        Long userId = 100L;

        List<OrderItem> items = List.of(
                OrderItem.create(201L, 2, 10000L),
                OrderItem.create(202L, 3, 5000L),
                OrderItem.create(203L, 1, 20000L)
        );
        Order order = Order.create(userId, items, new Price(55000L));
        order.assignId(orderId);
        order.markAsPaid();

        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
        given(orderRepository.save(any(Order.class))).willReturn(order);

        // when
        completeOrderUseCase.execute(orderId);

        // then
        verify(orderRepository).save(order);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("주문 상태 전환 시나리오: CREATED -> PAID -> COMPLETED")
    void 주문_상태_전환_시나리오() {
        // given
        Long orderId = 1L;
        Long userId = 100L;

        List<OrderItem> items = List.of(OrderItem.create(200L, 1, 10000L));
        Order order = Order.create(userId, items, new Price(10000L));
        order.assignId(orderId);

        // when & then - CREATED
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);

        // when & then - PAID
        order.markAsPaid();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);

        // when & then - COMPLETED
        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
        given(orderRepository.save(any(Order.class))).willReturn(order);

        completeOrderUseCase.execute(orderId);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }
}

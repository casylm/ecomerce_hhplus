package me.seyoung.ecomerce.application.order;

import me.seyoung.ecomerce.domain.order.Order;
import me.seyoung.ecomerce.domain.order.OrderItem;
import me.seyoung.ecomerce.domain.order.OrderRepository;
import me.seyoung.ecomerce.domain.order.OrderStatus;
import me.seyoung.ecomerce.domain.payment.Price;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CancelOrderUseCase 단위 테스트")
class CancleOrderUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private CancelOrderUseCase cancelOrderUseCase;

    @Test
    @DisplayName("주문 취소에 성공한다")
    void 주문_취소_성공() {
        // given
        Long orderId = 1L;
        Long userId = 100L;
        Long productId = 200L;
        int quantity = 2;

        List<OrderItem> items = List.of(OrderItem.create(productId, quantity, 10000L));
        Order order = Order.create(userId, items, new Price(20000L));
        order.assignId(orderId);

        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
        given(orderRepository.save(any(Order.class))).willReturn(order);

        // when
        cancelOrderUseCase.cancel(orderId, null, null);

        // then
        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(order);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    @DisplayName("존재하지 않는 주문을 취소하면 예외가 발생한다")
    void 존재하지_않는_주문_취소_시_예외() {
        // given
        Long orderId = 999L;
        given(orderRepository.findById(orderId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> cancelOrderUseCase.cancel(orderId, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 주문입니다");
    }
}

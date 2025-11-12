package me.seyoung.ecomerce.domain.order;

import me.seyoung.ecomerce.domain.payment.Price;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class OrderTest {

    @Test
    void 주문을_생성한다() {
        // given
        Long userId = 1L;
        List<OrderItem> items = Arrays.asList(
                new OrderItem(1L, 2, 10000L),
                new OrderItem(2L, 1, 5000L)
        );
        Price finalPrice = new Price(25000L);

        // when
        Order order = Order.create(userId, items, finalPrice);

        // then
        assertThat(order.getUserId()).isEqualTo(userId);
        assertThat(order.getItems()).hasSize(2);
        assertThat(order.getTotalPrice().getValue()).isEqualTo(25000L); // 20000 + 5000
        assertThat(order.getFinalPrice().getValue()).isEqualTo(25000L);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(order.getOrderedAt()).isNotNull();
    }

    @Test
    void 여러_상품으로_주문_생성_시_총액이_정확히_계산된다() {
        // given
        Long userId = 1L;
        List<OrderItem> items = Arrays.asList(
                new OrderItem(1L, 3, 10000L), // 30000
                new OrderItem(2L, 2, 15000L), // 30000
                new OrderItem(3L, 1, 5000L)   // 5000
        );
        Price finalPrice = new Price(65000L);

        // when
        Order order = Order.create(userId, items, finalPrice);

        // then
        assertThat(order.getTotalPrice().getValue()).isEqualTo(65000L);
    }

    @Test
    void 주문에_ID를_할당한다() {
        // given
        Long userId = 1L;
        List<OrderItem> items = Arrays.asList(
                new OrderItem(1L, 1, 10000L)
        );
        Price finalPrice = new Price(10000L);
        Order order = Order.create(userId, items, finalPrice);

        // when
        order.assignId(100L);

        // then
        assertThat(order.getId()).isEqualTo(100L);
    }

    @Test
    void 주문을_결제_완료_처리한다() {
        // given
        Long userId = 1L;
        List<OrderItem> items = Arrays.asList(
                new OrderItem(1L, 1, 10000L)
        );
        Price finalPrice = new Price(10000L);
        Order order = Order.create(userId, items, finalPrice);

        // when
        order.markAsPaid();

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void 이미_결제_완료된_주문을_다시_결제하면_예외가_발생한다() {
        // given
        Long userId = 1L;
        List<OrderItem> items = Arrays.asList(
                new OrderItem(1L, 1, 10000L)
        );
        Price finalPrice = new Price(10000L);
        Order order = Order.create(userId, items, finalPrice);
        order.markAsPaid();

        // when & then
        assertThatThrownBy(() -> order.markAsPaid())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 결제 완료된 주문입니다.");
    }

    @Test
    void 취소된_주문을_결제하면_예외가_발생한다() {
        // given
        Long userId = 1L;
        List<OrderItem> items = Arrays.asList(
                new OrderItem(1L, 1, 10000L)
        );
        Price finalPrice = new Price(10000L);
        Order order = Order.create(userId, items, finalPrice);
        order.cancel();

        // when & then
        assertThatThrownBy(() -> order.markAsPaid())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("취소된 주문은 결제할 수 없습니다.");
    }

    @Test
    void 주문을_취소한다() {
        // given
        Long userId = 1L;
        List<OrderItem> items = Arrays.asList(
                new OrderItem(1L, 1, 10000L)
        );
        Price finalPrice = new Price(10000L);
        Order order = Order.create(userId, items, finalPrice);

        // when
        order.cancel();

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void 이미_취소된_주문을_다시_취소하면_예외가_발생한다() {
        // given
        Long userId = 1L;
        List<OrderItem> items = Arrays.asList(
                new OrderItem(1L, 1, 10000L)
        );
        Price finalPrice = new Price(10000L);
        Order order = Order.create(userId, items, finalPrice);
        order.cancel();

        // when & then
        assertThatThrownBy(() -> order.cancel())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 취소된 주문입니다.");
    }

    @Test
    void 할인이_적용된_주문을_생성한다() {
        // given
        Long userId = 1L;
        List<OrderItem> items = Arrays.asList(
                new OrderItem(1L, 2, 10000L) // 총액 20000
        );
        Price finalPrice = new Price(18000L); // 할인 적용 후 18000

        // when
        Order order = Order.create(userId, items, finalPrice);

        // then
        assertThat(order.getTotalPrice().getValue()).isEqualTo(20000L);
        assertThat(order.getFinalPrice().getValue()).isEqualTo(18000L);
    }
}

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
                OrderItem.create(1L, 2, 10000L),
                OrderItem.create(2L, 1, 5000L)
        );
        Price finalPrice = new Price(25000L);

        // when
        Order order = Order.create(userId, items, finalPrice);

        // then
        assertThat(order.getUserId()).isEqualTo(userId);
        assertThat(order.getItems()).hasSize(2);
        assertThat(order.getTotalPrice()).isEqualTo(25000L); // 20000 + 5000
        assertThat(order.getFinalPrice()).isEqualTo(25000L);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(order.getOrderedAt()).isNotNull();
    }

    @Test
    void 여러_상품으로_주문_생성_시_총액이_정확히_계산된다() {
        // given
        Long userId = 1L;
        List<OrderItem> items = Arrays.asList(
                OrderItem.create(1L, 3, 10000L), // 30000
                OrderItem.create(2L, 2, 15000L), // 30000
                OrderItem.create(3L, 1, 5000L)   // 5000
        );
        Price finalPrice = new Price(65000L);

        // when
        Order order = Order.create(userId, items, finalPrice);

        // then
        assertThat(order.getTotalPrice()).isEqualTo(65000L);
    }

    @Test
    void 주문을_취소한다() {
        // given
        Long userId = 1L;
        List<OrderItem> items = Arrays.asList(
                OrderItem.create(1L, 1, 10000L)
        );
        Price finalPrice = new Price(10000L);
        Order order = Order.create(userId, items, finalPrice);

        // when
        order.cancel();

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }
}

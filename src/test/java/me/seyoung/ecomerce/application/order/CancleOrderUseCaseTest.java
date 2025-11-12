package me.seyoung.ecomerce.application.order;

import me.seyoung.ecomerce.domain.coupon.UserCoupon;
import me.seyoung.ecomerce.domain.coupon.UserCouponRepository;
import me.seyoung.ecomerce.domain.order.Order;
import me.seyoung.ecomerce.domain.order.OrderItem;
import me.seyoung.ecomerce.domain.order.OrderStatus;
import me.seyoung.ecomerce.domain.payment.Price;
import me.seyoung.ecomerce.domain.point.Point;
import me.seyoung.ecomerce.domain.point.PointRepository;
import me.seyoung.ecomerce.domain.product.ProductRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CancelOrderUseCase 단위 테스트")
class CancleOrderUseCaseTest {

    @Mock
    private InMemoryOrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PointRepository pointRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @InjectMocks
    private CancelOrderUseCase cancelOrderUseCase;

    @Test
    @DisplayName("주문을 취소하면 재고가 복구된다")
    void 주문_취소_시_재고_복구() {
        // given
        Long orderId = 1L;
        Long userId = 100L;
        Long productId = 200L;
        int quantity = 3;

        List<OrderItem> items = List.of(new OrderItem(productId, quantity, 10000L));
        Order order = Order.create(userId, items, new Price(30000L));
        order.assignId(orderId);

        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
        given(orderRepository.save(any(Order.class))).willReturn(order);

        // when
        cancelOrderUseCase.cancel(orderId);

        // then
        verify(productRepository).restoreStock(productId, quantity);
        verify(orderRepository).save(order);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    @DisplayName("주문 취소 시 사용한 포인트를 반환한다")
    void 주문_취소_시_포인트_반환() {
        // given
        Long orderId = 1L;
        Long userId = 100L;
        Long productId = 200L;
        Long usedPointAmount = 5000L;

        List<OrderItem> items = List.of(new OrderItem(productId, 2, 10000L));
        Order order = Order.create(userId, items, new Price(15000L));
        order.assignId(orderId);

        Point point = new Point(userId, 10000L);

        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
        given(pointRepository.findByUserId(userId)).willReturn(Optional.of(point));
        given(pointRepository.save(any(Point.class))).willReturn(point);
        given(orderRepository.save(any(Order.class))).willReturn(order);

        // when
        cancelOrderUseCase.cancel(orderId, null, usedPointAmount);

        // then
        verify(pointRepository).findByUserId(userId);
        verify(pointRepository).save(point);
        assertThat(point.getBalance()).isEqualTo(15000L); // 10000 + 5000
    }

    @Test
    @DisplayName("주문 취소 시 사용한 쿠폰 사용을 취소한다")
    void 주문_취소_시_쿠폰_사용_취소() {
        // given
        Long orderId = 1L;
        Long userId = 100L;
        Long productId = 200L;
        Long usedCouponId = 300L;

        List<OrderItem> items = List.of(new OrderItem(productId, 2, 10000L));
        Order order = Order.create(userId, items, new Price(18000L));
        order.assignId(orderId);

        UserCoupon userCoupon = new UserCoupon(userId, 1L);
        userCoupon.assignId(usedCouponId);
        userCoupon.use(); // 사용된 상태

        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
        given(userCouponRepository.findById(usedCouponId)).willReturn(Optional.of(userCoupon));
        given(userCouponRepository.save(any(UserCoupon.class))).willReturn(userCoupon);
        given(orderRepository.save(any(Order.class))).willReturn(order);

        // when
        cancelOrderUseCase.cancel(orderId, usedCouponId, 0L);

        // then
        verify(userCouponRepository).findById(usedCouponId);
        verify(userCouponRepository).save(userCoupon);
        assertThat(userCoupon.isAvailable()).isTrue(); // 사용 취소되어 다시 사용 가능
    }

    @Test
    @DisplayName("주문 취소 시 재고, 포인트, 쿠폰이 모두 복구된다")
    void 주문_취소_시_모든_리소스_복구() {
        // given
        Long orderId = 1L;
        Long userId = 100L;
        Long productId = 200L;
        Long usedCouponId = 300L;
        Long usedPointAmount = 3000L;
        int quantity = 2;

        List<OrderItem> items = List.of(new OrderItem(productId, quantity, 10000L));
        Order order = Order.create(userId, items, new Price(17000L));
        order.assignId(orderId);

        Point point = new Point(userId, 5000L);
        UserCoupon userCoupon = new UserCoupon(userId, 1L);
        userCoupon.assignId(usedCouponId);
        userCoupon.use();

        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
        given(pointRepository.findByUserId(userId)).willReturn(Optional.of(point));
        given(userCouponRepository.findById(usedCouponId)).willReturn(Optional.of(userCoupon));
        given(pointRepository.save(any(Point.class))).willReturn(point);
        given(userCouponRepository.save(any(UserCoupon.class))).willReturn(userCoupon);
        given(orderRepository.save(any(Order.class))).willReturn(order);

        // when
        cancelOrderUseCase.cancel(orderId, usedCouponId, usedPointAmount);

        // then
        verify(productRepository).restoreStock(productId, quantity);
        verify(pointRepository).save(point);
        verify(userCouponRepository).save(userCoupon);
        verify(orderRepository).save(order);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(point.getBalance()).isEqualTo(8000L); // 5000 + 3000
        assertThat(userCoupon.isAvailable()).isTrue();
    }

    @Test
    @DisplayName("여러 상품이 포함된 주문을 취소하면 모든 상품의 재고가 복구된다")
    void 여러_상품_주문_취소() {
        // given
        Long orderId = 1L;
        Long userId = 100L;

        List<OrderItem> items = List.of(
                new OrderItem(201L, 2, 10000L),
                new OrderItem(202L, 3, 5000L),
                new OrderItem(203L, 1, 20000L)
        );
        Order order = Order.create(userId, items, new Price(55000L));
        order.assignId(orderId);

        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
        given(orderRepository.save(any(Order.class))).willReturn(order);

        // when
        cancelOrderUseCase.cancel(orderId);

        // then
        verify(productRepository).restoreStock(201L, 2);
        verify(productRepository).restoreStock(202L, 3);
        verify(productRepository).restoreStock(203L, 1);
        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("존재하지 않는 주문을 취소하면 예외가 발생한다")
    void 존재하지_않는_주문_취소_시_예외() {
        // given
        Long orderId = 999L;
        given(orderRepository.findById(orderId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> cancelOrderUseCase.cancel(orderId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 주문입니다. orderId=" + orderId);
    }
}

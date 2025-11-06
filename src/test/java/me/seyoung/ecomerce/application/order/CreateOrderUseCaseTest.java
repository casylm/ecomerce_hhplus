package me.seyoung.ecomerce.application.order;

import me.seyoung.ecomerce.domain.coupon.Coupon;
import me.seyoung.ecomerce.domain.coupon.CouponRepository;
import me.seyoung.ecomerce.domain.coupon.UserCoupon;
import me.seyoung.ecomerce.domain.coupon.UserCouponRepository;
import me.seyoung.ecomerce.domain.order.Order;
import me.seyoung.ecomerce.domain.order.OrderItem;
import me.seyoung.ecomerce.domain.payment.Price;
import me.seyoung.ecomerce.domain.payment.PriceCalculationService;
import me.seyoung.ecomerce.domain.point.Point;
import me.seyoung.ecomerce.domain.point.PointRepository;
import me.seyoung.ecomerce.domain.product.Product;
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
@DisplayName("CreateOrderUseCase 단위 테스트")
class CreateOrderUseCaseTest {

    @Mock
    private InMemoryOrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @Mock
    private PointRepository pointRepository;

    @Mock
    private PriceCalculationService priceCalculationService;

    @InjectMocks
    private CreateOrderUseCase createOrderUseCase;

    @Test
    @DisplayName("재고가 충분하면 주문을 생성할 수 있다")
    void 재고_충분_시_주문_생성() {
        // given
        Long userId = 1L;
        Long productId = 100L;
        int quantity = 3;
        long pricePerItem = 10000L;

        List<OrderItem> items = List.of(new OrderItem(productId, quantity, pricePerItem));
        Product product = new Product(productId, "테스트 상품", pricePerItem, 10, "카테고리");

        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(priceCalculationService.calculate(any(), any(), any())).willReturn(new Price(30000L));

        Order mockOrder = Order.create(userId, items, new Price(30000L));
        mockOrder.assignId(1L);
        given(orderRepository.save(any(Order.class))).willReturn(mockOrder);

        // when
        Long orderId = createOrderUseCase.execute(userId, items);

        // then
        assertThat(orderId).isEqualTo(1L);
        verify(productRepository).deductStock(productId, quantity);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("재고가 부족하면 주문 생성에 실패한다")
    void 재고_부족_시_주문_생성_실패() {
        // given
        Long userId = 1L;
        Long productId = 100L;
        int quantity = 10;
        long pricePerItem = 10000L;

        List<OrderItem> items = List.of(new OrderItem(productId, quantity, pricePerItem));
        Product product = new Product(productId, "테스트 상품", pricePerItem, 5, "카테고리"); // 재고 5개

        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // when & then
        assertThatThrownBy(() -> createOrderUseCase.execute(userId, items))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("재고가 부족합니다");

        verify(productRepository, never()).deductStock(any(), anyInt());
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("쿠폰을 사용하여 주문을 생성할 수 있다")
    void 쿠폰_사용_주문_생성() {
        // given
        Long userId = 1L;
        Long productId = 100L;
        Long userCouponId = 200L;
        Long couponId = 300L;

        List<OrderItem> items = List.of(new OrderItem(productId, 2, 10000L));
        Product product = new Product(productId, "테스트 상품", 10000L, 10, "카테고리");

        UserCoupon userCoupon = new UserCoupon(userId, couponId);
        userCoupon.assignId(userCouponId);

        Coupon coupon = new Coupon("2000원 할인 쿠폰", 2000, 100);
        coupon.assignId(couponId);

        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(userCouponRepository.findById(userCouponId)).willReturn(Optional.of(userCoupon));
        given(couponRepository.findById(couponId)).willReturn(Optional.of(coupon));
        given(priceCalculationService.calculate(any(), any(), any())).willReturn(new Price(18000L));

        Order mockOrder = Order.create(userId, items, new Price(18000L));
        mockOrder.assignId(1L);
        given(orderRepository.save(any(Order.class))).willReturn(mockOrder);

        // when
        Long orderId = createOrderUseCase.execute(userId, userCouponId, items, 0L);

        // then
        assertThat(orderId).isEqualTo(1L);
        verify(userCouponRepository).save(userCoupon);
        assertThat(userCoupon.isAvailable()).isFalse(); // 쿠폰 사용됨
    }

    @Test
    @DisplayName("포인트를 사용하여 주문을 생성할 수 있다")
    void 포인트_사용_주문_생성() {
        // given
        Long userId = 1L;
        Long productId = 100L;
        long pointToUse = 5000L;

        List<OrderItem> items = List.of(new OrderItem(productId, 2, 10000L));
        Product product = new Product(productId, "테스트 상품", 10000L, 10, "카테고리");
        Point point = new Point(userId, 10000L);

        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(pointRepository.findByUserId(userId)).willReturn(Optional.of(point));
        given(priceCalculationService.calculate(any(), any(), any())).willReturn(new Price(15000L));

        Order mockOrder = Order.create(userId, items, new Price(15000L));
        mockOrder.assignId(1L);
        given(orderRepository.save(any(Order.class))).willReturn(mockOrder);

        // when
        Long orderId = createOrderUseCase.execute(userId, null, items, pointToUse);

        // then
        assertThat(orderId).isEqualTo(1L);
        verify(pointRepository).save(point);
        assertThat(point.getBalance()).isEqualTo(5000L); // 10000 - 5000
    }

    @Test
    @DisplayName("쿠폰과 포인트를 함께 사용하여 주문을 생성할 수 있다")
    void 쿠폰_포인트_함께_사용() {
        // given
        Long userId = 1L;
        Long productId = 100L;
        Long userCouponId = 200L;
        Long couponId = 300L;
        long pointToUse = 3000L;

        List<OrderItem> items = List.of(new OrderItem(productId, 2, 10000L));
        Product product = new Product(productId, "테스트 상품", 10000L, 10, "카테고리");

        UserCoupon userCoupon = new UserCoupon(userId, couponId);
        userCoupon.assignId(userCouponId);

        Coupon coupon = new Coupon("2000원 할인 쿠폰", 2000, 100);
        coupon.assignId(couponId);

        Point point = new Point(userId, 10000L);

        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(userCouponRepository.findById(userCouponId)).willReturn(Optional.of(userCoupon));
        given(couponRepository.findById(couponId)).willReturn(Optional.of(coupon));
        given(pointRepository.findByUserId(userId)).willReturn(Optional.of(point));
        given(priceCalculationService.calculate(any(), any(), any())).willReturn(new Price(15000L));

        Order mockOrder = Order.create(userId, items, new Price(15000L));
        mockOrder.assignId(1L);
        given(orderRepository.save(any(Order.class))).willReturn(mockOrder);

        // when
        Long orderId = createOrderUseCase.execute(userId, userCouponId, items, pointToUse);

        // then
        assertThat(orderId).isEqualTo(1L);
        verify(userCouponRepository).save(userCoupon);
        verify(pointRepository).save(point);
        assertThat(userCoupon.isAvailable()).isFalse();
        assertThat(point.getBalance()).isEqualTo(7000L); // 10000 - 3000
    }

    @Test
    @DisplayName("여러 상품을 포함한 주문을 생성할 수 있다")
    void 여러_상품_주문_생성() {
        // given
        Long userId = 1L;

        List<OrderItem> items = List.of(
                new OrderItem(101L, 2, 10000L),
                new OrderItem(102L, 3, 5000L),
                new OrderItem(103L, 1, 20000L)
        );

        Product product1 = new Product(101L, "상품1", 10000L, 10, "카테고리");
        Product product2 = new Product(102L, "상품2", 5000L, 10, "카테고리");
        Product product3 = new Product(103L, "상품3", 20000L, 10, "카테고리");

        given(productRepository.findById(101L)).willReturn(Optional.of(product1));
        given(productRepository.findById(102L)).willReturn(Optional.of(product2));
        given(productRepository.findById(103L)).willReturn(Optional.of(product3));
        given(priceCalculationService.calculate(any(), any(), any())).willReturn(new Price(55000L));

        Order mockOrder = Order.create(userId, items, new Price(55000L));
        mockOrder.assignId(1L);
        given(orderRepository.save(any(Order.class))).willReturn(mockOrder);

        // when
        Long orderId = createOrderUseCase.execute(userId, items);

        // then
        assertThat(orderId).isEqualTo(1L);
        verify(productRepository).deductStock(101L, 2);
        verify(productRepository).deductStock(102L, 3);
        verify(productRepository).deductStock(103L, 1);
    }

    @Test
    @DisplayName("존재하지 않는 상품으로 주문 생성 시 예외가 발생한다")
    void 존재하지_않는_상품_주문_생성_시_예외() {
        // given
        Long userId = 1L;
        Long productId = 999L;

        List<OrderItem> items = List.of(new OrderItem(productId, 1, 10000L));
        given(productRepository.findById(productId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> createOrderUseCase.execute(userId, items))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 상품입니다");
    }

    @Test
    @DisplayName("존재하지 않는 쿠폰으로 주문 생성 시 예외가 발생한다")
    void 존재하지_않는_쿠폰_주문_생성_시_예외() {
        // given
        Long userId = 1L;
        Long productId = 100L;
        Long userCouponId = 999L;

        List<OrderItem> items = List.of(new OrderItem(productId, 1, 10000L));
        Product product = new Product(productId, "테스트 상품", 10000L, 10, "카테고리");

        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(userCouponRepository.findById(userCouponId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> createOrderUseCase.execute(userId, userCouponId, items, 0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 사용자 쿠폰입니다");

        verify(productRepository).deductStock(productId, 1); // 재고는 이미 차감됨
    }

    @Test
    @DisplayName("다른 사용자의 쿠폰으로 주문 생성 시 예외가 발생한다")
    void 다른_사용자_쿠폰_사용_시_예외() {
        // given
        Long userId = 1L;
        Long otherUserId = 2L;
        Long productId = 100L;
        Long userCouponId = 200L;
        Long couponId = 300L;

        List<OrderItem> items = List.of(new OrderItem(productId, 1, 10000L));
        Product product = new Product(productId, "테스트 상품", 10000L, 10, "카테고리");

        UserCoupon userCoupon = new UserCoupon(otherUserId, couponId); // 다른 사용자의 쿠폰
        userCoupon.assignId(userCouponId);

        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(userCouponRepository.findById(userCouponId)).willReturn(Optional.of(userCoupon));

        // when & then
        assertThatThrownBy(() -> createOrderUseCase.execute(userId, userCouponId, items, 0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("본인 소유의 쿠폰만 사용할 수 있습니다");
    }

    @Test
    @DisplayName("이미 사용된 쿠폰으로 주문 생성 시 예외가 발생한다")
    void 사용된_쿠폰_주문_생성_시_예외() {
        // given
        Long userId = 1L;
        Long productId = 100L;
        Long userCouponId = 200L;
        Long couponId = 300L;

        List<OrderItem> items = List.of(new OrderItem(productId, 1, 10000L));
        Product product = new Product(productId, "테스트 상품", 10000L, 10, "카테고리");

        UserCoupon userCoupon = new UserCoupon(userId, couponId);
        userCoupon.assignId(userCouponId);
        userCoupon.use(); // 이미 사용됨

        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(userCouponRepository.findById(userCouponId)).willReturn(Optional.of(userCoupon));

        // when & then
        assertThatThrownBy(() -> createOrderUseCase.execute(userId, userCouponId, items, 0L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 사용된 쿠폰입니다");
    }

    @Test
    @DisplayName("포인트가 부족하면 주문 생성에 실패한다")
    void 포인트_부족_시_주문_생성_실패() {
        // given
        Long userId = 1L;
        Long productId = 100L;
        long pointToUse = 10000L;

        List<OrderItem> items = List.of(new OrderItem(productId, 1, 10000L));
        Product product = new Product(productId, "테스트 상품", 10000L, 10, "카테고리");
        Point point = new Point(userId, 5000L); // 포인트 잔액 부족

        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(pointRepository.findByUserId(userId)).willReturn(Optional.of(point));

        // when & then
        assertThatThrownBy(() -> createOrderUseCase.execute(userId, null, items, pointToUse))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("포인트가 부족합니다");
    }

    @Test
    @DisplayName("포인트가 없는 사용자가 포인트를 사용하려고 하면 예외가 발생한다")
    void 포인트_없는_사용자_포인트_사용_시_예외() {
        // given
        Long userId = 1L;
        Long productId = 100L;
        long pointToUse = 5000L;

        List<OrderItem> items = List.of(new OrderItem(productId, 1, 10000L));
        Product product = new Product(productId, "테스트 상품", 10000L, 10, "카테고리");

        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(pointRepository.findByUserId(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> createOrderUseCase.execute(userId, null, items, pointToUse))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용자의 포인트가 존재하지 않습니다");
    }
}

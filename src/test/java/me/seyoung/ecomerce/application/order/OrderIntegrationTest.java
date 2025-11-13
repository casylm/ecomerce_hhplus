package me.seyoung.ecomerce.application.order;

import me.seyoung.ecomerce.domain.order.Order;
import me.seyoung.ecomerce.domain.order.OrderItem;
import me.seyoung.ecomerce.domain.order.OrderRepository;
import me.seyoung.ecomerce.domain.order.OrderStatus;
import me.seyoung.ecomerce.domain.product.Product;
import me.seyoung.ecomerce.domain.product.ProductRepository;
import me.seyoung.ecomerce.domain.user.User;
import me.seyoung.ecomerce.domain.user.UserRepository;
import me.seyoung.ecomerce.application.AbstractContainerBaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("주문 통합 테스트")
class OrderIntegrationTest extends AbstractContainerBaseTest {

    @Autowired
    CreateOrderUseCase createOrderUseCase;

    @Autowired
    CompleteOrderUseCase completeOrderUseCase;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderRepository orderRepository;

    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        // 테스트 사용자 생성
        testUser = new User("주문테스트유저");
        testUser = userRepository.save(testUser);

        // 테스트 상품 생성
        testProduct = new Product(null, "테스트상품", 10000L, 100, "테스트카테고리");
        testProduct = productRepository.save(testProduct);
    }

    @Test
    @Transactional
    @DisplayName("주문이 정상적으로 생성된다")
    void 주문_생성_성공() {
        // given
        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem orderItem = OrderItem.create(testProduct.getId(), 2, testProduct.getPrice());
        orderItems.add(orderItem);

        // when
        Long orderId = createOrderUseCase.execute(testUser.getId(), orderItems);

        // then
        assertThat(orderId).isNotNull();

        // DB에서 직접 확인
        Order savedOrder = orderRepository.findById(orderId).orElseThrow();
        assertThat(savedOrder.getUserId()).isEqualTo(testUser.getId());
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(savedOrder.getTotalPrice()).isEqualTo(20000L); // 10000 * 2
        assertThat(savedOrder.getItems()).hasSize(1);
    }

    @Test
    @Transactional
    @DisplayName("결제 완료 후 주문이 완료 처리된다")
    void 주문_완료_처리_성공() {
        // given - 주문 생성
        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem orderItem = OrderItem.create(testProduct.getId(), 1, testProduct.getPrice());
        orderItems.add(orderItem);

        Long orderId = createOrderUseCase.execute(testUser.getId(), orderItems);

        // 결제 완료 상태로 변경
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.markAsPaid();
        orderRepository.save(order);

        // when - 주문 완료 처리
        completeOrderUseCase.execute(orderId);

        // then
        Order completedOrder = orderRepository.findById(orderId).orElseThrow();
        assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }
}

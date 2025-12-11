package me.seyoung.ecomerce.application.payment;

import me.seyoung.ecomerce.application.AbstractContainerBaseTest;
import me.seyoung.ecomerce.application.order.CreateOrderUseCase;
import me.seyoung.ecomerce.application.payment.event.PaymentCompletedEvent;
import me.seyoung.ecomerce.domain.order.OrderItem;
import me.seyoung.ecomerce.domain.payment.Pay;
import me.seyoung.ecomerce.domain.payment.PaymentInfo;
import me.seyoung.ecomerce.domain.product.Product;
import me.seyoung.ecomerce.domain.product.ProductRepository;
import me.seyoung.ecomerce.domain.user.User;
import me.seyoung.ecomerce.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("결제 완료 비동기 이벤트 테스트")
@Import(PaymentEventAsyncTest.TestEventListener.class)
class PaymentEventAsyncTest extends AbstractContainerBaseTest {

    @Autowired
    CreatePaymentUseCase createPaymentUseCase;

    @Autowired
    CreateOrderUseCase createOrderUseCase;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    TestEventListener testEventListener;

    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testUser = new User("테스트유저");
        testUser = userRepository.save(testUser);

        testProduct = new Product(null, "테스트상품", 10000L, 100, "테스트카테고리");
        testProduct = productRepository.save(testProduct);

        testEventListener.reset();
    }

    @Test
    @DisplayName("결제 완료 후 @TransactionalEventListener로 비동기 이벤트가 처리된다")
    void 결제완료_비동기_이벤트_처리_테스트() throws InterruptedException {
        // given - 주문 생성
        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem orderItem = OrderItem.create(testProduct.getId(), 1, testProduct.getPrice());
        orderItems.add(orderItem);

        Long orderId = createOrderUseCase.execute(testUser.getId(), orderItems);

        // when - 결제 생성
        Pay payCommand = new Pay(orderId, testProduct.getPrice(), testUser.getId(), null, null);
        PaymentInfo.Result result = createPaymentUseCase.execute(payCommand);

        // then - 결제는 즉시 완료
        assertThat(result).isNotNull();
        assertThat(result.paymentId()).isNotNull();

        // 비동기 이벤트 처리 대기 (최대 5초)
        // @TransactionalEventListener(AFTER_COMMIT) + @Async로 인해
        // 트랜잭션 커밋 후 별도 스레드에서 실행됨
        boolean eventReceived = testEventListener.latch.await(5, TimeUnit.SECONDS);

        assertThat(eventReceived).isTrue();
        assertThat(testEventListener.eventProcessed.get()).isTrue();

        PaymentCompletedEvent capturedEvent = testEventListener.capturedEvent.get();
        assertThat(capturedEvent).isNotNull();
        assertThat(capturedEvent.paymentId()).isEqualTo(result.paymentId());
        assertThat(capturedEvent.orderId()).isEqualTo(orderId);
    }

    @Component
    static class TestEventListener {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean eventProcessed = new AtomicBoolean(false);
        AtomicReference<PaymentCompletedEvent> capturedEvent = new AtomicReference<>();

        @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
        public void handlePaymentEvent(PaymentCompletedEvent event) {
            System.out.println("테스트 이벤트 리스너 호출 - paymentId: " + event.paymentId() +
                ", orderId: " + event.orderId());

            capturedEvent.set(event);
            eventProcessed.set(true);
            latch.countDown();
        }

        public void reset() {
            latch = new CountDownLatch(1);
            eventProcessed.set(false);
            capturedEvent.set(null);
        }
    }
}

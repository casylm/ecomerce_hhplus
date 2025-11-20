package me.seyoung.ecomerce.application.payment;

import me.seyoung.ecomerce.application.order.CreateOrderUseCase;
import me.seyoung.ecomerce.domain.order.OrderItem;
import me.seyoung.ecomerce.domain.payment.Pay;
import me.seyoung.ecomerce.domain.payment.Payment;
import me.seyoung.ecomerce.domain.payment.PaymentInfo;
import me.seyoung.ecomerce.domain.payment.PaymentRepository;
import me.seyoung.ecomerce.domain.payment.PaymentStatus;
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

@DisplayName("결제 통합 테스트")
class PaymentIntegrationTest extends AbstractContainerBaseTest {

    @Autowired
    CreatePaymentUseCase createPaymentUseCase;

    @Autowired
    CanclePaymentUseCase canclePaymentUseCase;

    @Autowired
    CreateOrderUseCase createOrderUseCase;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    PaymentRepository paymentRepository;

    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        // 테스트 사용자 생성
        testUser = new User("결제테스트유저");
        testUser = userRepository.save(testUser);

        // 테스트 상품 생성
        testProduct = new Product(null, "결제테스트상품", 15000L, 100, "테스트카테고리");
        testProduct = productRepository.save(testProduct);
    }

    @Test
    @Transactional
    @DisplayName("결제가 정상적으로 생성된다")
    void 결제_생성_성공() {
        // given - 주문 생성
        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem orderItem = OrderItem.create(testProduct.getId(), 2, testProduct.getPrice());
        orderItems.add(orderItem);

        Long orderId = createOrderUseCase.execute(testUser.getId(), orderItems);

        // when - 결제 생성
        Pay payCommand = new Pay(orderId, 30000L, testUser.getId(), null, null);
        PaymentInfo.Result result = createPaymentUseCase.execute(payCommand);

        // then
        assertThat(result).isNotNull();
        assertThat(result.orderId()).isEqualTo(orderId);
        assertThat(result.amount()).isEqualTo(30000L);
        assertThat(result.status()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(result.paidAt()).isNotNull();

        // DB에서 직접 확인
        Payment savedPayment = paymentRepository.findByOrderId(orderId).orElseThrow();
        assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(savedPayment.getAmount()).isEqualTo(30000L);
    }

    @Test
    @Transactional
    @DisplayName("결제 생성 후 취소가 정상적으로 처리된다")
    void 결제_취소_성공() {
        // given - 주문 및 결제 생성
        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem orderItem = OrderItem.create(testProduct.getId(), 1, testProduct.getPrice());
        orderItems.add(orderItem);

        Long orderId = createOrderUseCase.execute(testUser.getId(), orderItems);

        Pay payCommand = new Pay(orderId, 15000L, testUser.getId(), null, null);
        PaymentInfo.Result paymentResult = createPaymentUseCase.execute(payCommand);

        // when - 결제 취소
        PaymentInfo.Result cancelResult = canclePaymentUseCase.execute(paymentResult.paymentId());

        // then
        assertThat(cancelResult.status()).isEqualTo(PaymentStatus.CANCELLED);
        assertThat(cancelResult.cancelledAt()).isNotNull();

        // DB에서 직접 확인
        Payment cancelledPayment = paymentRepository.findById(paymentResult.paymentId()).orElseThrow();
        assertThat(cancelledPayment.getStatus()).isEqualTo(PaymentStatus.CANCELLED);
    }

    @Test
    @DisplayName("[핵심 동시성] 재고 10개인 상품에 20명이 동시 결제 시 비관적 락으로 10명만 성공한다")
    void 동시_결제시_재고_초과_방지_테스트() throws InterruptedException {
        // given
        int initialStock = 10;
        int threadCount = 20;

        // 재고 10개인 상품 생성
        Product limitedProduct = new Product(null, "한정판상품", 10000L, initialStock, "한정판");
        limitedProduct = productRepository.save(limitedProduct);

        // 20명의 사용자 생성
        List<User> users = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            User user = new User("동시결제유저" + i);
            users.add(userRepository.save(user));
        }

        java.util.concurrent.ExecutorService executorService = java.util.concurrent.Executors.newFixedThreadPool(threadCount);
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(threadCount);

        java.util.concurrent.atomic.AtomicInteger successCount = new java.util.concurrent.atomic.AtomicInteger(0);
        java.util.concurrent.atomic.AtomicInteger failCount = new java.util.concurrent.atomic.AtomicInteger(0);

        Product finalProduct = limitedProduct;

        // when - 20명이 동시에 같은 상품 1개씩 결제 시도
        for (int i = 0; i < threadCount; i++) {
            final int userIndex = i;
            executorService.submit(() -> {
                try {
                    // 각 사용자가 주문 생성
                    List<OrderItem> orderItems = new ArrayList<>();
                    OrderItem orderItem = OrderItem.create(finalProduct.getId(), 1, finalProduct.getPrice());
                    orderItems.add(orderItem);

                    Long orderId = createOrderUseCase.execute(users.get(userIndex).getId(), orderItems);

                    // 결제 시도 (재고 차감 발생)
                    Pay payCommand = new Pay(orderId, finalProduct.getPrice(), users.get(userIndex).getId(), null, null);
                    createPaymentUseCase.execute(payCommand);

                    successCount.incrementAndGet();
                } catch (IllegalStateException e) {
                    // 재고 부족 예외
                    if (e.getMessage().contains("재고가 부족합니다")) {
                        failCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then - 핵심 검증: 정확히 재고만큼만 성공
        assertThat(successCount.get()).isEqualTo(initialStock);
        assertThat(failCount.get()).isEqualTo(threadCount - initialStock);

        // DB에서 최종 재고 확인 - 정확히 0이어야 함
        Product finalProductState = productRepository.findById(finalProduct.getId()).orElseThrow();
        assertThat(finalProductState.getStock()).isZero();

        System.out.println("=== 결제 동시성 테스트 결과 ===");
        System.out.println("초기 재고: " + initialStock);
        System.out.println("동시 결제 시도: " + threadCount + "명");
        System.out.println("성공한 결제: " + successCount.get());
        System.out.println("실패한 결제: " + failCount.get());
        System.out.println("최종 재고: " + finalProductState.getStock());
    }
}

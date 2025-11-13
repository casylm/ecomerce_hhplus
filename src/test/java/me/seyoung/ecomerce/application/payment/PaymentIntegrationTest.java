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
}

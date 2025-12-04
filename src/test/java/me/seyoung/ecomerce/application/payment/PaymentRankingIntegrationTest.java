package me.seyoung.ecomerce.application.payment;

import me.seyoung.ecomerce.application.AbstractContainerBaseTest;
import me.seyoung.ecomerce.application.order.CreateOrderUseCase;
import me.seyoung.ecomerce.domain.order.OrderItem;
import me.seyoung.ecomerce.domain.payment.Pay;
import me.seyoung.ecomerce.domain.payment.PaymentInfo;
import me.seyoung.ecomerce.domain.product.Product;
import me.seyoung.ecomerce.domain.product.ProductRepository;
import me.seyoung.ecomerce.domain.rank.ProductRankingItem;
import me.seyoung.ecomerce.domain.rank.ProductRankingRepository;
import me.seyoung.ecomerce.domain.rank.RankingPeriod;
import me.seyoung.ecomerce.domain.user.User;
import me.seyoung.ecomerce.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("결제 완료 시 상품 랭킹 증가 테스트")
class PaymentRankingIntegrationTest extends AbstractContainerBaseTest {

    @Autowired
    CreatePaymentUseCase createPaymentUseCase;

    @Autowired
    CreateOrderUseCase createOrderUseCase;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductRankingRepository productRankingRepository;

    private User testUser;
    private Product testProduct1;
    private Product testProduct2;

    @BeforeEach
    void setUp() {
        // 테스트 사용자 생성
        testUser = new User("랭킹테스트유저");
        testUser = userRepository.save(testUser);

        // 테스트 상품 생성
        testProduct1 = new Product(null, "랭킹테스트상품1", 10000L, 100, "테스트카테고리1");
        testProduct1 = productRepository.save(testProduct1);

        testProduct2 = new Product(null, "랭킹테스트상품2", 20000L, 100, "테스트카테고리2");
        testProduct2 = productRepository.save(testProduct2);
    }

    @Test
    @DisplayName("단일 상품 결제 완료 시 Redis에 판매량이 증가한다")
    void 단일_상품_결제_시_랭킹_증가() {
        // given - 주문 생성
        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem orderItem = OrderItem.create(testProduct1.getId(), 1, testProduct1.getPrice());
        orderItems.add(orderItem);

        Long orderId = createOrderUseCase.execute(testUser.getId(), orderItems);

        // when - 결제 완료
        Pay payCommand = new Pay(orderId, 10000L, testUser.getId(), null, null);
        PaymentInfo.Result result = createPaymentUseCase.execute(payCommand);

        // then - 결제 성공 확인
        assertThat(result).isNotNull();

        // Redis에서 랭킹 확인 - ProductRankingRepository를 통해 조회
        List<ProductRankingItem> ranking = productRankingRepository.findTopN(RankingPeriod.DAILY, 10);

        // 결제한 상품이 랭킹에 포함되어 있는지 확인
        assertThat(ranking).isNotEmpty();
        ProductRankingItem item = ranking.stream()
                .filter(r -> r.productId().equals(testProduct1.getId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("결제한 상품이 랭킹에 없습니다"));

        assertThat(item.score()).isEqualTo(1.0); // 수량 1개 구매

        System.out.println("=== 단일 상품 결제 후 Redis 랭킹 ===");
        System.out.println("상품 ID: " + testProduct1.getId());
        System.out.println("판매량: " + item.score());
    }

    @Test
    @DisplayName("여러 개 수량 구매 시 Redis에 수량만큼 판매량이 증가한다")
    void 여러개_수량_구매_시_랭킹_증가() {
        // given - 수량 5개 주문 생성
        int quantity = 5;
        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem orderItem = OrderItem.create(testProduct1.getId(), quantity, testProduct1.getPrice());
        orderItems.add(orderItem);

        Long orderId = createOrderUseCase.execute(testUser.getId(), orderItems);

        // when - 결제 완료
        Pay payCommand = new Pay(orderId, 10000L * quantity, testUser.getId(), null, null);
        createPaymentUseCase.execute(payCommand);

        // then - Redis에서 랭킹 확인
        List<ProductRankingItem> ranking = productRankingRepository.findTopN(RankingPeriod.DAILY, 10);

        ProductRankingItem item = ranking.stream()
                .filter(r -> r.productId().equals(testProduct1.getId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("결제한 상품이 랭킹에 없습니다"));

        assertThat(item.score()).isEqualTo((double) quantity);

        System.out.println("=== 여러 개 수량 구매 후 Redis 랭킹 ===");
        System.out.println("상품 ID: " + testProduct1.getId());
        System.out.println("구매 수량: " + quantity);
        System.out.println("판매량: " + item.score());
    }

    @Test
    @DisplayName("여러 상품 동시 결제 시 각 상품의 판매량이 개별적으로 증가한다")
    void 여러_상품_동시_결제_시_랭킹_증가() {
        // given - 2개 상품 주문 생성
        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem orderItem1 = OrderItem.create(testProduct1.getId(), 3, testProduct1.getPrice());
        OrderItem orderItem2 = OrderItem.create(testProduct2.getId(), 2, testProduct2.getPrice());
        orderItems.add(orderItem1);
        orderItems.add(orderItem2);

        Long orderId = createOrderUseCase.execute(testUser.getId(), orderItems);

        // when - 결제 완료
        long totalAmount = (10000L * 3) + (20000L * 2);
        Pay payCommand = new Pay(orderId, totalAmount, testUser.getId(), null, null);
        createPaymentUseCase.execute(payCommand);

        // then - Redis에서 각 상품의 랭킹 확인
        List<ProductRankingItem> ranking = productRankingRepository.findTopN(RankingPeriod.DAILY, 10);

        ProductRankingItem item1 = ranking.stream()
                .filter(r -> r.productId().equals(testProduct1.getId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("상품1이 랭킹에 없습니다"));

        ProductRankingItem item2 = ranking.stream()
                .filter(r -> r.productId().equals(testProduct2.getId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("상품2가 랭킹에 없습니다"));

        assertThat(item1.score()).isEqualTo(3.0);
        assertThat(item2.score()).isEqualTo(2.0);

        System.out.println("=== 여러 상품 동시 결제 후 Redis 랭킹 ===");
        System.out.println("상품1 ID: " + testProduct1.getId() + ", 판매량: " + item1.score());
        System.out.println("상품2 ID: " + testProduct2.getId() + ", 판매량: " + item2.score());
    }

    @Test
    @DisplayName("여러 번 결제 시 판매량이 누적되어 증가한다")
    void 여러번_결제_시_판매량_누적() {
        // given & when - 첫 번째 결제
        List<OrderItem> orderItems1 = new ArrayList<>();
        OrderItem orderItem1 = OrderItem.create(testProduct1.getId(), 2, testProduct1.getPrice());
        orderItems1.add(orderItem1);
        Long orderId1 = createOrderUseCase.execute(testUser.getId(), orderItems1);
        Pay payCommand1 = new Pay(orderId1, 20000L, testUser.getId(), null, null);
        createPaymentUseCase.execute(payCommand1);

        // 두 번째 결제
        List<OrderItem> orderItems2 = new ArrayList<>();
        OrderItem orderItem2 = OrderItem.create(testProduct1.getId(), 3, testProduct1.getPrice());
        orderItems2.add(orderItem2);
        Long orderId2 = createOrderUseCase.execute(testUser.getId(), orderItems2);
        Pay payCommand2 = new Pay(orderId2, 30000L, testUser.getId(), null, null);
        createPaymentUseCase.execute(payCommand2);

        // then - Redis에서 누적된 판매량 확인
        List<ProductRankingItem> ranking = productRankingRepository.findTopN(RankingPeriod.DAILY, 10);

        ProductRankingItem item = ranking.stream()
                .filter(r -> r.productId().equals(testProduct1.getId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("결제한 상품이 랭킹에 없습니다"));

        assertThat(item.score()).isEqualTo(5.0); // 2 + 3 = 5

        System.out.println("=== 여러 번 결제 후 Redis 랭킹 ===");
        System.out.println("상품 ID: " + testProduct1.getId());
        System.out.println("누적 판매량: " + item.score() + " (2 + 3)");
    }

    @Test
    @DisplayName("결제 후 Redis에 랭킹 데이터가 존재한다")
    void Redis_랭킹_데이터_존재_확인() {
        // given - 주문 및 결제 생성
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(OrderItem.create(testProduct1.getId(), 1, testProduct1.getPrice()));
        Long orderId = createOrderUseCase.execute(testUser.getId(), orderItems);

        // when - 결제 완료
        createPaymentUseCase.execute(new Pay(orderId, 10000L, testUser.getId(), null, null));

        // then - Redis에서 랭킹 조회 가능 확인
        List<ProductRankingItem> ranking = productRankingRepository.findTopN(RankingPeriod.DAILY, 10);

        assertThat(ranking).isNotEmpty();
        assertThat(ranking).anyMatch(item -> item.productId().equals(testProduct1.getId()));

        System.out.println("=== Redis 랭킹 데이터 존재 확인 ===");
        System.out.println("랭킹 데이터 개수: " + ranking.size());
        System.out.println("상품 ID: " + testProduct1.getId() + " 포함 여부: true");
    }
}

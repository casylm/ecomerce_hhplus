package me.seyoung.ecomerce.application.product;

import me.seyoung.ecomerce.application.AbstractContainerBaseTest;
import me.seyoung.ecomerce.application.product.dto.ProductInfo;
import me.seyoung.ecomerce.domain.product.Product;
import me.seyoung.ecomerce.domain.product.ProductRepository;
import me.seyoung.ecomerce.facade.RedissonLockStockFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("상품 재고 복구 동시성 통합 테스트")
class RestoreStockConcurrencyIntegrationTest extends AbstractContainerBaseTest {

    @Autowired
    private RedissonLockStockFacade redissonLockStockFacade;

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        // 테스트용 상품 생성 (초기 재고 0개 - 복구 테스트를 위해)
        testProduct = new Product(null, "재고 복구 테스트 상품", 10000L, 0, "테스트 카테고리");
        testProduct = productRepository.save(testProduct);
    }

    @Test
    @DisplayName("[핵심 동시성] 동시에 100명이 재고 복구를 요청하면 분산 락으로 인해 정확히 재고가 복구된다")
    void 동시에_여러_요청이_재고를_복구해도_락으로_인해_정확히_재고가_복구된다() throws InterruptedException {
        // given
        int threadCount = 100;  // 동시 요청 수
        int restoreQuantity = 1; // 각 요청당 복구 수량
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when - 100개의 스레드가 동시에 재고 복구 시도
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    ProductInfo.StockIncrease result = redissonLockStockFacade.restore(testProduct.getId(), restoreQuantity);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        Product updatedProduct = productRepository.findById(testProduct.getId()).orElseThrow();

        // 분산 락이 제대로 동작했다면, 100개의 요청이 모두 성공하고 재고는 정확히 100이 되어야 함
        assertThat(successCount.get()).isEqualTo(100);
        assertThat(failCount.get()).isEqualTo(0);
        assertThat(updatedProduct.getStock()).isEqualTo(100);

        System.out.println("=== 재고 복구 동시성 테스트 결과 ===");
        System.out.println("초기 재고: 0개");
        System.out.println("성공한 요청: " + successCount.get());
        System.out.println("실패한 요청: " + failCount.get());
        System.out.println("최종 재고: " + updatedProduct.getStock());
        System.out.println("예상 재고: 100");
    }

    @Test
    @DisplayName("[복합 동시성] 동시에 재고 차감과 복구가 섞여서 발생해도 최종 재고는 정확하다")
    void 재고_차감과_복구가_동시에_발생해도_최종_재고는_정확하다() throws InterruptedException {
        // given
        // 초기 재고 100개로 설정
        Product product = new Product(null, "복합 동시성 테스트 상품", 20000L, 100, "테스트");
        Product savedProduct = productRepository.save(product);

        int deductThreadCount = 50;  // 차감 요청 50개
        int restoreThreadCount = 50; // 복구 요청 50개
        int totalThreadCount = deductThreadCount + restoreThreadCount;

        ExecutorService executorService = Executors.newFixedThreadPool(50);
        CountDownLatch latch = new CountDownLatch(totalThreadCount);

        AtomicInteger deductSuccessCount = new AtomicInteger(0);
        AtomicInteger restoreSuccessCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when - 50개 차감, 50개 복구 동시에 실행
        // 차감 요청 50개
        for (int i = 0; i < deductThreadCount; i++) {
            executorService.submit(() -> {
                try {
                    ProductInfo.StockDecrease result = redissonLockStockFacade.decrease(savedProduct.getId(), 1);
                    deductSuccessCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        // 복구 요청 50개
        for (int i = 0; i < restoreThreadCount; i++) {
            executorService.submit(() -> {
                try {
                    ProductInfo.StockIncrease result = redissonLockStockFacade.restore(savedProduct.getId(), 1);
                    restoreSuccessCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        Product finalProduct = productRepository.findById(savedProduct.getId()).orElseThrow();

        // 초기 재고 100 - 차감 50 + 복구 50 = 100
        int expectedStock = 100 - deductSuccessCount.get() + restoreSuccessCount.get();
        assertThat(finalProduct.getStock()).isEqualTo(expectedStock);

        System.out.println("=== 재고 차감/복구 복합 동시성 테스트 결과 ===");
        System.out.println("초기 재고: 100개");
        System.out.println("차감 성공: " + deductSuccessCount.get() + "개");
        System.out.println("복구 성공: " + restoreSuccessCount.get() + "개");
        System.out.println("실패: " + failCount.get() + "개");
        System.out.println("최종 재고: " + finalProduct.getStock());
        System.out.println("예상 재고: " + expectedStock);
    }
}

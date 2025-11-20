package me.seyoung.ecomerce.application.product;

import me.seyoung.ecomerce.application.AbstractContainerBaseTest;
import me.seyoung.ecomerce.application.product.dto.ProductInfo;
import me.seyoung.ecomerce.domain.product.Product;
import me.seyoung.ecomerce.domain.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("상품 재고 차감 동시성 통합 테스트")
class DeductStockConcurrencyIntegrationTest extends AbstractContainerBaseTest {

    @Autowired
    private DeductStockUseCase deductStockUseCase;

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        // 테스트용 상품 생성 (초기 재고 100개)
        testProduct = new Product(null, "테스트 상품", 10000L, 100, "테스트 카테고리");
        testProduct = productRepository.save(testProduct);
    }

    @Test
    @DisplayName("[핵심 동시성] 동시에 100명이 재고 차감을 요청하면 비관적 락으로 인해 정확히 재고가 차감된다")
    void 동시에_여러_요청이_재고를_차감해도_락으로_인해_정확히_재고가_차감된다() throws InterruptedException {
        // given
        int threadCount = 100;  // 동시 요청 수
        int deductQuantity = 1; // 각 요청당 차감 수량
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when - 100개의 스레드가 동시에 재고 차감 시도
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    ProductInfo.StockDecrease result = deductStockUseCase.execute(testProduct.getId(), deductQuantity);
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

        // 비관적 락이 제대로 동작했다면, 100개의 요청이 모두 성공하고 재고는 정확히 0이 되어야 함
        assertThat(successCount.get()).isEqualTo(100);
        assertThat(failCount.get()).isEqualTo(0);
        assertThat(updatedProduct.getStock()).isEqualTo(0);

        System.out.println("=== 동시성 테스트 결과 ===");
        System.out.println("성공한 요청: " + successCount.get());
        System.out.println("실패한 요청: " + failCount.get());
        System.out.println("최종 재고: " + updatedProduct.getStock());
        System.out.println("예상 재고: 0");
    }

    @Test
    @DisplayName("재고보다 많은 동시 요청이 들어오면 일부는 실패하고 재고 부족 예외가 발생한다")
    void 재고보다_많은_동시_요청시_일부는_실패한다() throws InterruptedException {
        // given
        int threadCount = 150;  // 동시 요청 수 (재고 100개보다 많음)
        int deductQuantity = 1;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when - 150개의 스레드가 동시에 재고 차감 시도
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    deductStockUseCase.execute(testProduct.getId(), deductQuantity);
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

        // then
        Product updatedProduct = productRepository.findById(testProduct.getId()).orElseThrow();

        // 100개의 요청만 성공하고, 50개는 재고 부족으로 실패해야 함
        assertThat(successCount.get()).isEqualTo(100);
        assertThat(failCount.get()).isEqualTo(50);
        assertThat(updatedProduct.getStock()).isEqualTo(0);

        System.out.println("=== 재고 부족 동시성 테스트 결과 ===");
        System.out.println("성공한 요청: " + successCount.get());
        System.out.println("실패한 요청 (재고 부족): " + failCount.get());
        System.out.println("최종 재고: " + updatedProduct.getStock());
    }

    @Test
    @DisplayName("재고 50개에 100명이 동시 요청 시 정확히 50명만 성공한다")
    void 재고_50개_100명_동시_요청() throws InterruptedException {
        // given
        Product limitedProduct = new Product(null, "한정판 상품", 50000L, 50, "한정판");
        Product savedLimitedProduct = productRepository.save(limitedProduct);

        int threadCount = 100;
        int deductQuantity = 1;
        ExecutorService executorService = Executors.newFixedThreadPool(50);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when - 100명이 동시에 재고 차감 시도
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    deductStockUseCase.execute(savedLimitedProduct.getId(), deductQuantity);
                    successCount.incrementAndGet();
                } catch (IllegalStateException e) {
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

        // then - 정확히 50명만 성공
        assertThat(successCount.get()).isEqualTo(50);
        assertThat(failCount.get()).isEqualTo(50);

        Product finalProduct = productRepository.findById(savedLimitedProduct.getId()).orElseThrow();
        assertThat(finalProduct.getStock()).isZero();

        System.out.println("=== 한정판 상품 동시성 테스트 결과 ===");
        System.out.println("초기 재고: 50개");
        System.out.println("동시 요청: " + threadCount + "명");
        System.out.println("성공: " + successCount.get() + "명");
        System.out.println("실패: " + failCount.get() + "명");
        System.out.println("최종 재고: " + finalProduct.getStock());
    }
}

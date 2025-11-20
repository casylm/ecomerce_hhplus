package me.seyoung.ecomerce.application.point;

import me.seyoung.ecomerce.domain.point.Point;
import me.seyoung.ecomerce.domain.point.PointRepository;
import me.seyoung.ecomerce.domain.user.User;
import me.seyoung.ecomerce.domain.user.UserRepository;
import me.seyoung.ecomerce.application.AbstractContainerBaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("포인트 통합 테스트 - Testcontainers MySQL")
class PointIntegrationTest extends AbstractContainerBaseTest {

    @Autowired
    ChargePointUseCase chargePointUseCase;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PointRepository pointRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // 테스트 사용자 생성 및 저장
        testUser = new User("세영");
        testUser = userRepository.save(testUser);
    }

    @Test
    @Transactional
    @DisplayName("실제 DB에 사용자와 포인트가 저장되고 조회된다")
    void 실제_DB_기반_포인트_충전_성공() {
        // when
        PointInfo.ChargePointResponse response = chargePointUseCase.execute(testUser.getId(), 500L);

        // then - 응답 검증
        assertThat(response.getUserId()).isEqualTo(testUser.getId());
        assertThat(response.getCurrentPoint()).isEqualTo(500L);

        // then - Point 엔티티 직접 조회 및 DB 값 확인
        Point savedPoint = pointRepository.findByUserId(testUser.getId()).orElseThrow();
        assertThat(savedPoint.getBalance()).isEqualTo(500L);
        assertThat(savedPoint.getUserId()).isEqualTo(testUser.getId());
    }

    @Test
    @Transactional
    @DisplayName("포인트를 여러 번 충전하면 잔액이 누적된다")
    void 포인트_여러번_충전_성공() {
        // given
        chargePointUseCase.execute(testUser.getId(), 1000L);

        // when
        chargePointUseCase.execute(testUser.getId(), 500L);
        PointInfo.ChargePointResponse response = chargePointUseCase.execute(testUser.getId(), 300L);

        // then - 응답 검증
        assertThat(response.getCurrentPoint()).isEqualTo(1800L);

        // then - DB 값 직접 확인
        Point savedPoint = pointRepository.findByUserId(testUser.getId()).orElseThrow();
        assertThat(savedPoint.getBalance()).isEqualTo(1800L);
    }

    @Test
    @DisplayName("[핵심 동시성] 동일 사용자가 2번 동시에 포인트 충전 시 낙관적 락으로 정확히 처리된다")
    void 동일_사용자_동시_포인트_충전시_낙관적_락_동작() throws InterruptedException {
        // given
        chargePointUseCase.execute(testUser.getId(), 1000L); // 초기 포인트 1000

        int threadCount = 2;
        long chargeAmount = 500L;
        java.util.concurrent.ExecutorService executorService = java.util.concurrent.Executors.newFixedThreadPool(threadCount);
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(threadCount);

        java.util.concurrent.atomic.AtomicInteger successCount = new java.util.concurrent.atomic.AtomicInteger(0);
        java.util.concurrent.atomic.AtomicInteger failCount = new java.util.concurrent.atomic.AtomicInteger(0);

        // when - 2개의 스레드가 동시에 포인트 충전 시도
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    chargePointUseCase.execute(testUser.getId(), chargeAmount);
                    successCount.incrementAndGet();
                } catch (org.springframework.orm.ObjectOptimisticLockingFailureException e) {
                    // 낙관적 락 예외 발생
                    failCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then - 낙관적 락으로 인해 일부 실패 가능, 하지만 최종 잔액은 정확해야 함
        Point finalPoint = pointRepository.findByUserId(testUser.getId()).orElseThrow();

        // 성공한 충전 횟수만큼 포인트가 증가해야 함
        long expectedBalance = 1000L + (successCount.get() * chargeAmount);
        assertThat(finalPoint.getBalance()).isEqualTo(expectedBalance);

        System.out.println("=== 포인트 동시성 테스트 결과 ===");
        System.out.println("성공한 충전: " + successCount.get());
        System.out.println("실패한 충전 (낙관적 락): " + failCount.get());
        System.out.println("최종 포인트 잔액: " + finalPoint.getBalance());
        System.out.println("예상 포인트 잔액: " + expectedBalance);
    }
}

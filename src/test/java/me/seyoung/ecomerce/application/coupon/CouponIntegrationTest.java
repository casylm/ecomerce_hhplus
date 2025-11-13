package me.seyoung.ecomerce.application.coupon;

import me.seyoung.ecomerce.domain.coupon.Coupon;
import me.seyoung.ecomerce.domain.coupon.CouponRepository;
import me.seyoung.ecomerce.domain.coupon.UserCouponRepository;
import me.seyoung.ecomerce.domain.user.User;
import me.seyoung.ecomerce.domain.user.UserRepository;
import me.seyoung.ecomerce.application.AbstractContainerBaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 쿠폰 도메인 통합 테스트
 * 실제 JPA Repository를 사용하여 DB 연동 및 동시성 제어를 검증
 * Testcontainers를 사용하여 실제 MySQL 데이터베이스 환경에서 테스트
 */
class CouponIntegrationTest extends AbstractContainerBaseTest {

    @Autowired
    private IssueCouponUseCase issueCouponUseCase;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    @DisplayName("[통합] 쿠폰 발급 성공 - 실제 DB를 통해 쿠폰이 발급되고 재고가 차감된다")
    void successfullyIssueCoupon() {
        // given
        User user = new User("통합테스트유저");
        User savedUser = userRepository.save(user);

        Coupon coupon = new Coupon("5천원 할인 쿠폰", 5000, 10);
        Coupon savedCoupon = couponRepository.save(coupon);

        // when
        CouponInfo.CouponIssueResult result = issueCouponUseCase.execute(savedUser.getId(), savedCoupon.getId());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(savedUser.getId());
        assertThat(result.getCouponId()).isEqualTo(savedCoupon.getId());
        assertThat(result.getDiscountAmount()).isEqualTo(5000);

        // DB에서 직접 조회하여 재고 확인
        Coupon updatedCoupon = couponRepository.findById(savedCoupon.getId())
                .orElseThrow(() -> new AssertionError("쿠폰을 찾을 수 없습니다"));
        assertThat(updatedCoupon.getQuantity()).isEqualTo(9); // 10 - 1 = 9

        // 유저 쿠폰이 실제로 저장되었는지 확인 (userId로 조회)
        assertThat(userCouponRepository.findByUserId(savedUser.getId())).hasSize(1);
    }

    @Test
    @DisplayName("[통합] 동시성 제어 - 100명이 동시 요청 시 재고만큼만 발급된다 (비관적 락)")
    void preventRaceConditionWithPessimisticLock() throws InterruptedException {
        // given
        int couponStock = 10;
        int threadCount = 100;

        // 쿠폰 생성
        Coupon coupon = new Coupon("선착순 할인 쿠폰", 3000, couponStock);
        Coupon savedCoupon = couponRepository.save(coupon);

        // 사용자 100명 생성
        for (int i = 0; i < threadCount; i++) {
            User user = new User("동시성테스트유저" + i);
            userRepository.save(user);
        }

        // 동시성 제어를 위한 설정
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when - 100개의 스레드가 동시에 쿠폰 발급 시도
        for (int i = 0; i < threadCount; i++) {
            final long userId = i + 1;
            executorService.submit(() -> {
                try {
                    issueCouponUseCase.execute(userId, savedCoupon.getId());
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        // 모든 스레드 작업 완료 대기
        latch.await();
        executorService.shutdown();

        // then
        assertThat(successCount.get()).isEqualTo(couponStock); // 정확히 재고만큼만 성공
        assertThat(failCount.get()).isEqualTo(threadCount - couponStock); // 나머지는 실패

        // DB에서 직접 조회하여 최종 재고 확인
        Coupon finalCoupon = couponRepository.findById(savedCoupon.getId())
                .orElseThrow(() -> new AssertionError("쿠폰을 찾을 수 없습니다"));
        assertThat(finalCoupon.getQuantity()).isZero(); // 재고는 0이어야 함

        // 발급된 유저 쿠폰 수 확인 (성공한 유저들의 쿠폰을 개별적으로 확인)
        int issuedCouponCount = 0;
        for (int i = 0; i < threadCount; i++) {
            long userId = i + 1;
            if (userCouponRepository.findByUserId(userId).size() > 0) {
                issuedCouponCount++;
            }
        }
        assertThat(issuedCouponCount).isEqualTo(couponStock);
    }

    @Test
    @Transactional
    @DisplayName("[통합] 쿠폰 재고 소진 - 재고가 없을 때 발급 시도하면 예외가 발생한다")
    void failToIssueCouponWhenOutOfStock() {
        // given
        User user = new User("재고테스트유저");
        User savedUser = userRepository.save(user);

        Coupon coupon = new Coupon("품절 쿠폰", 2000, 0); // 재고 0
        Coupon savedCoupon = couponRepository.save(coupon);

        // when & then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            issueCouponUseCase.execute(savedUser.getId(), savedCoupon.getId());
        });

        assertThat(exception.getMessage()).isEqualTo("쿠폰 재고가 소진되었습니다.");

        // DB에서 직접 조회하여 재고가 변하지 않았는지 확인
        Coupon unchangedCoupon = couponRepository.findById(savedCoupon.getId())
                .orElseThrow(() -> new AssertionError("쿠폰을 찾을 수 없습니다"));
        assertThat(unchangedCoupon.getQuantity()).isZero();

        // 유저 쿠폰이 생성되지 않았는지 확인 (userId로 조회)
        assertThat(userCouponRepository.findByUserId(savedUser.getId())).isEmpty();
    }
}

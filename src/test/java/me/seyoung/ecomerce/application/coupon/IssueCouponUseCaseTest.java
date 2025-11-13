package me.seyoung.ecomerce.application.coupon;

import me.seyoung.ecomerce.domain.coupon.Coupon;
import me.seyoung.ecomerce.domain.coupon.CouponRepository;
import me.seyoung.ecomerce.domain.coupon.UserCoupon;
import me.seyoung.ecomerce.domain.coupon.UserCouponRepository;
import me.seyoung.ecomerce.domain.user.User;
import me.seyoung.ecomerce.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class IssueCouponUseCaseTest {

    @InjectMocks
    private IssueCouponUseCase issueCouponUseCase;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("쿠폰을 발급한다")
    @Transactional
    void issueCoupon() {
        // given
        User user = new User("테스트유저");
        User savedUser = userRepository.save(user);

        Coupon coupon = new Coupon("3천원 할인 쿠폰", 3000, 5);
        Coupon savedCoupon = couponRepository.save(coupon);

        // when
        CouponInfo.CouponIssueResult result = issueCouponUseCase.execute(savedUser.getId(), savedCoupon.getId());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(savedUser.getId());
        assertThat(result.getCouponId()).isEqualTo(savedCoupon.getId());
        assertThat(result.getDiscountAmount()).isEqualTo(3000);

        // 재고가 1개 줄어들었는지 확인
        Coupon updatedCoupon = couponRepository.findById(savedCoupon.getId()).orElseThrow();
        assertThat(updatedCoupon.getQuantity()).isEqualTo(4);
    }

    @Test
    @DisplayName("재고가 없으면 쿠폰 발급을 하지 않는다")
    @Transactional
    void cannotIssueCouponWhenOutOfStock() {
        // given
        User user = new User("테스트유저");
        User savedUser = userRepository.save(user);

        Coupon coupon = new Coupon("재고없음 쿠폰", 3000, 0); // 재고 0
        Coupon savedCoupon = couponRepository.save(coupon);

        // when & then
        assertThrows(IllegalStateException.class, () -> {
            issueCouponUseCase.execute(savedUser.getId(), savedCoupon.getId());
        });
    }

    @Test
    @DisplayName("동시에 100명이 쿠폰 발급 요청시 재고 10개만 발급된다 (Race Condition 방지)")
    void concurrentCouponIssue() throws InterruptedException {
        // given
        int couponStock = 10; // 쿠폰 재고
        int threadCount = 100; // 동시 요청 수

        // 쿠폰 생성
        Coupon coupon = new Coupon("선착순 쿠폰", 5000, couponStock);
        Coupon savedCoupon = couponRepository.save(coupon);

        // 사용자 100명 생성
        for (int i = 0; i < threadCount; i++) {
            User user = new User("유저" + i);
            userRepository.save(user);
        }

        // 동시성 제어를 위한 CountDownLatch
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 성공/실패 카운터
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
        assertThat(successCount.get()).isEqualTo(couponStock); // 정확히 10개만 성공
        assertThat(failCount.get()).isEqualTo(threadCount - couponStock); // 나머지 90개는 실패

        // 쿠폰 재고 확인
        Coupon finalCoupon = couponRepository.findById(savedCoupon.getId()).orElseThrow();
        assertThat(finalCoupon.getQuantity()).isZero(); // 재고는 0이어야 함

        // 발급된 유저 쿠폰 수 확인
        // assertThat(userCouponRepository.findAll().size()).isEqualTo(couponStock);
    }
}
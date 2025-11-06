package me.seyoung.ecomerce.application.coupon;

import me.seyoung.ecomerce.domain.coupon.Coupon;
import me.seyoung.ecomerce.domain.coupon.CouponRepository;
import me.seyoung.ecomerce.domain.coupon.UserCoupon;
import me.seyoung.ecomerce.domain.coupon.UserCouponRepository;
import me.seyoung.ecomerce.domain.user.User;
import me.seyoung.ecomerce.domain.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplyCouponUseCaseTest {
    @Mock
    private CouponRepository couponRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @InjectMocks
    private ApplyCouponUseCase applyCouponUseCase;

    @Test
    @DisplayName("쿠폰을 정상적으로 사용한다")
    void apply_coupon_success() {
        // given
        Long userId = 1L;
        Long couponId = 10L;
        Long userCouponId = 100L;

        UserCoupon userCoupon = new UserCoupon(userId, couponId);
        userCoupon.assignId(userCouponId);
        Coupon coupon = new Coupon("3천원 할인 쿠폰", 3000, 10);

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User(userId, "tester")));
        when(userCouponRepository.findById(userCouponId)).thenReturn(Optional.of(userCoupon));
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        when(userCouponRepository.save(any(UserCoupon.class))).thenReturn(userCoupon);

        // when
        CouponInfo.CouponUseResult result = applyCouponUseCase.execute(userId, userCouponId);

        // then (책임 검증)
        Assertions.assertThat(result.getDiscountAmount()).isEqualTo(3000L); // 반환 데이터 확인
        verify(userCouponRepository).save(any(UserCoupon.class));  // 저장 호출 확인
    }

    @Test
    @DisplayName("이미 사용된 쿠폰은 사용할 수 없다")
    void apply_coupon_fail_used_coupon() {
        // given
        Long userId = 1L;
        Long userCouponId = 100L;
        Long couponId = 10L;

        UserCoupon userCoupon = new UserCoupon(userId, couponId);
        userCoupon.assignId(userCouponId);
        userCoupon.use(); // 이미 사용된 상태로 변경

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(new User(userId, "tester")));

        when(userCouponRepository.findById(userCouponId))
                .thenReturn(Optional.of(userCoupon));

        // when & then
        assertThatThrownBy(() -> applyCouponUseCase.execute(userId, userCouponId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 사용된 쿠폰입니다.");

        // 저장은 절대 일어나면 안 됨
        verify(userCouponRepository, never()).save(any());
    }
}
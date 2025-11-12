package me.seyoung.ecomerce.application.coupon;

import me.seyoung.ecomerce.domain.coupon.Coupon;
import me.seyoung.ecomerce.domain.coupon.CouponRepository;
import me.seyoung.ecomerce.domain.coupon.UserCoupon;
import me.seyoung.ecomerce.domain.coupon.UserCouponRepository;
import me.seyoung.ecomerce.infrastructure.coupon.InMemoryCouponRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IssueCouponUseCaseTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @InjectMocks
    private IssueCouponUseCase issueCouponUseCase;

    @Test
    void 쿠폰을_발급한다() {
        // given
        Long userId = 1L;
        Long couponId = 10L;

        Coupon coupon = new Coupon("3천원 할인 쿠폰", 3000, 5);
        coupon.assignId(couponId);

        UserCoupon savedUserCoupon = new UserCoupon(userId, couponId);
        savedUserCoupon.assignId(100L);

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        when(userCouponRepository.save(any(UserCoupon.class))).thenReturn(savedUserCoupon);

        // when
        CouponInfo.CouponIssueResult result = issueCouponUseCase.execute(userId, couponId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getCouponId()).isEqualTo(couponId);
        assertThat(result.getDiscountAmount()).isEqualTo(3000);

        verify(couponRepository).findById(couponId);
        verify(couponRepository).save(coupon);  // ✅ 재고 감소 반영 확인
        verify(userCouponRepository).save(any(UserCoupon.class));
    }

    @Test
    void 재고가없으면_쿠폰발급을_하지않는다() {

    }
}
package me.seyoung.ecomerce.application.coupon;

import me.seyoung.ecomerce.domain.coupon.Coupon;
import me.seyoung.ecomerce.domain.coupon.CouponRepository;
import me.seyoung.ecomerce.domain.coupon.UserCoupon;
import me.seyoung.ecomerce.domain.coupon.UserCouponRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCouponListUseCaseTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @InjectMocks
    private GetCouponListUseCase getCouponListUseCase;

    @Test
    void 사용자의_쿠폰목록이_조회된다() {
        // given
        Long userId = 1L;

        // 사용자 보유 쿠폰 2개
        UserCoupon userCoupon1 = new UserCoupon(userId, 10L);
        userCoupon1.assignId(100L);
        UserCoupon userCoupon2 = new UserCoupon(userId, 20L);
        userCoupon2.assignId(200L);

        Coupon coupon1 = new Coupon("신년할인 쿠폰", 10000, 1);
        Coupon coupon2 = new Coupon("신규가입 쿠폰", 5000, 1);

        coupon1.assignId(10L);
        coupon2.assignId(20L);

        when(userCouponRepository.findByUserId(userId))
                .thenReturn(List.of(userCoupon1, userCoupon2));

        // 쿠폰 엔티티 조회 Mock
        when(couponRepository.findById(10L))
                .thenReturn(Optional.of(coupon1));
        when(couponRepository.findById(20L))
                .thenReturn(Optional.of(coupon2));

        // when 유스케이스 실행
        CouponInfo.Coupons result = getCouponListUseCase.execute(userId);

        // then 검증
        assertThat(result).isNotNull();
        assertThat(result.getCoupons()).hasSize(2)
                .extracting("userCouponId")
                .containsExactlyInAnyOrder(100L, 200L);

        assertThat(result.getCoupons())
                .extracting("couponId")
                .containsExactlyInAnyOrder(10L, 20L);

        // repository 호출 검증
        verify(userCouponRepository).findByUserId(userId);
    }
}
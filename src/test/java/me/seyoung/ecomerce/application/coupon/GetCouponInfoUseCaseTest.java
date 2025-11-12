package me.seyoung.ecomerce.application.coupon;

import me.seyoung.ecomerce.domain.coupon.Coupon;
import me.seyoung.ecomerce.domain.coupon.CouponRepository;
import me.seyoung.ecomerce.domain.coupon.UserCoupon;
import me.seyoung.ecomerce.domain.coupon.UserCouponRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetCouponInfoUseCaseTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @InjectMocks
    private GetCouponInfoUseCase getCouponInfoUseCase;

    @Test
    @DisplayName("쿠폰 아이디로 쿠폰을 조회한다")
    void 쿠폰_아이디로_쿠폰을_조회한다() {
        // given
        Long userId = 1L;
        Long userCouponId = 100L;
        Long couponId = 10L;

        UserCoupon userCoupon = new UserCoupon(userId, couponId);
        userCoupon.assignId(userCouponId);

        Coupon coupon = new Coupon("3천원 할인 쿠폰", 3000, 10);
        coupon.assignId(couponId);

        when(userCouponRepository.findById(userCouponId))
                .thenReturn(Optional.of(userCoupon));

        when(couponRepository.findById(couponId))
                .thenReturn(Optional.of(coupon));

        // when
        CouponInfo.CouponDetailInfo result = getCouponInfoUseCase.execute(userId, userCouponId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCouponId()).isEqualTo(couponId);
        assertThat(result.getDiscountAmount()).isEqualTo(3000);

        verify(userCouponRepository).findById(userCouponId);
        verify(couponRepository).findById(couponId);
    }
    @Test
    @DisplayName("사용자의 쿠폰이 아니면 조회할 수 없다")
    void 사용자의_쿠폰이_아니면_조회할_수_없다() {
        // UserCoupon 이 조회됨
        // 하지만 userCoupon.userId ≠ userId
        // 발생 예외 메시지
        // 쿠폰 조회(findById)는 호출되지 않아야 함
        // given
        Long requestUserId = 1L;     // 요청한 사용자 ID
        Long actualOwnerId = 2L;     // 실제 쿠폰 소유자 ID
        Long userCouponId = 100L;
        Long couponId = 10L;

        // UserCoupon 은 존재하지만 userId 가 다름
        UserCoupon userCoupon = new UserCoupon(actualOwnerId, couponId);
        userCoupon.assignId(userCouponId);

        when(userCouponRepository.findById(userCouponId))
                .thenReturn(Optional.of(userCoupon));

        // when & then
        assertThatThrownBy(() -> getCouponInfoUseCase.execute(requestUserId, userCouponId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 사용자의 쿠폰이 아닙니다.");

        // 쿠폰 조회는 호출되면 안됨
        verify(couponRepository, never()).findById(anyLong());
    }
}
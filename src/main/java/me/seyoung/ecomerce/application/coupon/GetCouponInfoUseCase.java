package me.seyoung.ecomerce.application.coupon;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.domain.coupon.Coupon;
import me.seyoung.ecomerce.domain.coupon.CouponRepository;
import me.seyoung.ecomerce.domain.coupon.UserCoupon;
import me.seyoung.ecomerce.domain.coupon.UserCouponRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetCouponInfoUseCase {
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;

    public CouponInfo.CouponDetailInfo execute(Long userId, Long userCouponId) {
        // 1. UserCoupon 조회
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 쿠폰입니다."));

        // 2. 권한 검증 (해당 사용자의 쿠폰인지 확인)
        if (!userCoupon.isOwnedBy(userId)) {
            throw new IllegalArgumentException("해당 사용자의 쿠폰이 아닙니다.");
        }

        // 3. Coupon 조회
        Coupon coupon = couponRepository.findById(userCoupon.getCouponId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰입니다."));

        // 4. 응답 반환
        return CouponInfo.CouponDetailInfo.from(userCoupon, coupon);
    }
}

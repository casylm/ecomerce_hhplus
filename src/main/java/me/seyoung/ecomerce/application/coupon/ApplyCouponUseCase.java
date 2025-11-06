package me.seyoung.ecomerce.application.coupon;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.domain.coupon.Coupon;
import me.seyoung.ecomerce.domain.coupon.CouponRepository;
import me.seyoung.ecomerce.domain.coupon.UserCoupon;
import me.seyoung.ecomerce.domain.coupon.UserCouponRepository;
import me.seyoung.ecomerce.domain.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplyCouponUseCase {

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final UserRepository userRepository;

    public CouponInfo.CouponUseResult execute(Long userId, Long userCouponId) {
        // 1. 유효한 사용자 검증
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 2. UserCoupon 조회
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 쿠폰입니다."));

        // 3. 권한 검증 (해당 사용자의 쿠폰인지 확인)
        if (!userCoupon.isOwnedBy(userId)) {
            throw new IllegalArgumentException("해당 사용자의 쿠폰이 아닙니다.");
        }

        // 4. 사용 가능 여부 확인
        if (!userCoupon.isAvailable()) {
            throw new IllegalStateException("이미 사용된 쿠폰입니다.");
        }

        // 5. Coupon 조회
        Coupon coupon = couponRepository.findById(userCoupon.getCouponId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰입니다."));

        // 6. 쿠폰 사용 처리 및 저장
        userCoupon.use();
        UserCoupon savedCoupon = userCouponRepository.save(userCoupon);

        // 7. 응답 반환
        return CouponInfo.CouponUseResult.of(savedCoupon, coupon);
    }
}

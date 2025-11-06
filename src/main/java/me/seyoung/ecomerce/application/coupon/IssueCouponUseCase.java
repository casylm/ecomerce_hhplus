package me.seyoung.ecomerce.application.coupon;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.domain.coupon.Coupon;
import me.seyoung.ecomerce.domain.coupon.CouponRepository;
import me.seyoung.ecomerce.domain.coupon.UserCoupon;
import me.seyoung.ecomerce.domain.coupon.UserCouponRepository;
import me.seyoung.ecomerce.domain.user.User;
import me.seyoung.ecomerce.domain.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IssueCouponUseCase {
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final UserRepository userRepository;

    public CouponInfo.CouponIssueResult execute(Long userId, Long couponId) {
        // 1. 사용자 아이디 유효성 체크
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 2. 쿠폰 아이디 유효성 체크
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰입니다."));

        // 3. 쿠폰 재고 확인
        if (!coupon.hasStock()) {
            throw new IllegalStateException("쿠폰 재고가 소진되었습니다.");
        }

        // 4. 쿠폰 재고 차감
        coupon.decreaseQuantity();
        couponRepository.save(coupon);

        // 5. 유저 쿠폰 저장 (발급)
        UserCoupon userCoupon = new UserCoupon(userId, couponId);
        UserCoupon savedUserCoupon = userCouponRepository.save(userCoupon);

        // 6. 응답 반환
        return CouponInfo.CouponIssueResult.of(savedUserCoupon, coupon);
    }
}

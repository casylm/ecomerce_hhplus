package me.seyoung.ecomerce.application.coupon;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.domain.coupon.Coupon;
import me.seyoung.ecomerce.domain.coupon.CouponRepository;
import me.seyoung.ecomerce.domain.coupon.UserCoupon;
import me.seyoung.ecomerce.domain.coupon.UserCouponRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetCouponListUseCase {
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;

    public CouponInfo.Coupons execute(Long userId) {
        // 1. 사용자의 모든 UserCoupon 조회
        List<UserCoupon> userCoupons = userCouponRepository.findByUserId(userId);

        // 2. 각 UserCoupon에 대한 Coupon 정보 조회 및 CouponDetailInfo 생성
        List<CouponInfo.CouponDetailInfo> couponInfoList = userCoupons.stream()
                .map(userCoupon -> {
                    Coupon coupon = couponRepository.findById(userCoupon.getCouponId())
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰입니다."));
                    return CouponInfo.CouponDetailInfo.from(userCoupon, coupon);
                })
                .toList();

        // 3. 일급 컬렉션으로 반환
        return CouponInfo.Coupons.from(couponInfoList);
    }
}

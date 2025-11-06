package me.seyoung.ecomerce.application.coupon;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.seyoung.ecomerce.domain.coupon.Coupon;
import me.seyoung.ecomerce.domain.coupon.UserCoupon;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponInfo {

    /**
     * 쿠폰 상세 정보
     */
    @Getter
    @Builder
    public static class CouponDetailInfo {
        private final Long couponId;
        private final Long userCouponId;
        private final String name;
        private final int discountAmount;
        private final boolean used;
        private final LocalDateTime issuedAt;
        private final LocalDateTime usedAt;

        public static CouponDetailInfo from(UserCoupon userCoupon, Coupon coupon) {
            return CouponDetailInfo.builder()
                    .couponId(coupon.getId())
                    .userCouponId(userCoupon.getId())
                    .name(coupon.getName())
                    .discountAmount(coupon.getDiscountAmount())
                    .used(userCoupon.isUsed())
                    .issuedAt(userCoupon.getIssuedAt())
                    .usedAt(userCoupon.getUsedAt())
                    .build();
        }
    }

    /**
     * 쿠폰 목록
     */
    @Getter
    public static class Coupons {
        private final List<CouponDetailInfo> coupons;

        private Coupons(List<CouponDetailInfo> coupons) {
            this.coupons = coupons;
        }

        public static Coupons from(List<CouponDetailInfo> couponList) {
            return new Coupons(couponList);
        }
    }

    /**
     * 쿠폰 사용
     */
    @Getter
    @Builder
    public static class CouponUseResult {
        private final Long userCouponId;
        private final Long couponId;
        private final Long userId;
        private final int discountAmount;
        private final LocalDateTime usedAt;

        public static CouponUseResult of(UserCoupon userCoupon, Coupon coupon) {
            return CouponUseResult.builder()
                    .userCouponId(userCoupon.getId())
                    .couponId(userCoupon.getCouponId())
                    .userId(userCoupon.getUserId())
                    .discountAmount(coupon.getDiscountAmount())
                    .usedAt(userCoupon.getUsedAt())
                    .build();
        }
    }

    /**
     * 쿠폰 발급
     */
    @Getter
    @Builder
    public static class CouponIssueResult {
        private final Long userCouponId;
        private final Long userId;
        private final Long couponId;
        private final String couponName;
        private final int discountAmount;
        private final LocalDateTime issuedAt;

        public static CouponIssueResult of(UserCoupon userCoupon, Coupon coupon) {
            return CouponIssueResult.builder()
                    .userCouponId(userCoupon.getId())
                    .userId(userCoupon.getUserId())
                    .couponId(coupon.getId())
                    .couponName(coupon.getName())
                    .discountAmount(coupon.getDiscountAmount())
                    .issuedAt(userCoupon.getIssuedAt())
                    .build();
        }
    }
}

package me.seyoung.ecomerce.presentation.coupon.dto;

import me.seyoung.ecomerce.application.coupon.CouponInfo;

import java.time.LocalDateTime;

public record UseCouponResponse(
        Long userCouponId,
        Long couponId,
        Long userId,
        int discountAmount,
        LocalDateTime usedAt
) {
    public static UseCouponResponse from(CouponInfo.CouponUseResult couponInfo) {
        return new UseCouponResponse(
                couponInfo.getUserCouponId(),
                couponInfo.getCouponId(),
                couponInfo.getUserId(),
                couponInfo.getDiscountAmount(),
                couponInfo.getUsedAt()
        );
    }
}

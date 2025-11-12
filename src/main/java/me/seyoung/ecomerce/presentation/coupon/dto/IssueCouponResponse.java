package me.seyoung.ecomerce.presentation.coupon.dto;

import me.seyoung.ecomerce.application.coupon.CouponInfo;

import java.time.LocalDateTime;

public record IssueCouponResponse(
        Long userCouponId,
        Long userId,
        Long couponId,
        String couponName,
        int discountAmount,
        LocalDateTime issuedAt
) {
    public static IssueCouponResponse from(CouponInfo.CouponIssueResult couponInfo) {
        return new IssueCouponResponse(
                couponInfo.getUserCouponId(),
                couponInfo.getUserId(),
                couponInfo.getCouponId(),
                couponInfo.getCouponName(),
                couponInfo.getDiscountAmount(),
                couponInfo.getIssuedAt()
        );
    }
}

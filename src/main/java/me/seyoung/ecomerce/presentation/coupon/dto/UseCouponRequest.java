package me.seyoung.ecomerce.presentation.coupon.dto;

public record UseCouponRequest(
        Long userId,
        Long userCouponId
) {
}

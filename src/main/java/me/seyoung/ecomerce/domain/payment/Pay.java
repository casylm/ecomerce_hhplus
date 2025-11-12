package me.seyoung.ecomerce.domain.payment;

public record Pay(
        Long orderId,
        Long amount,
        Long userId,
        Long userCouponId // nullable 허용
) {}

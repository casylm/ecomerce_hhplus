package me.seyoung.ecomerce.domain.payment;

/**
 * 결제 요청 커맨드
 */
public record Pay(
        Long orderId,
        Long amount,
        Long userId,
        Long userCouponId, // nullable 허용
        Long pointToUse    // nullable 허용
) {}

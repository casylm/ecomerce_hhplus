package me.seyoung.ecomerce.presentation.payment.dto;

public record CreatePaymentRequest(
        Long orderId,
        Long amount,
        Long userId,
        Long userCouponId,  // nullable
        Long pointToUse     // nullable
) {
}

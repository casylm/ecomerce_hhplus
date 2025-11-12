package me.seyoung.ecomerce.presentation.order.dto;

public record CancelOrderRequest(
        Long usedCouponId,
        Long usedPointAmount
) {
}

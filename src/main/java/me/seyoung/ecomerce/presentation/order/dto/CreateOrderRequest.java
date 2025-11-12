package me.seyoung.ecomerce.presentation.order.dto;

import java.util.List;

public record CreateOrderRequest(
        Long userId,
        List<OrderItemRequest> items,
        Long userCouponId,
        Long pointToUse
) {
}

package me.seyoung.ecomerce.presentation.order.dto;

public record OrderItemRequest(
        Long productId,
        int quantity,
        long pricePerItem
) {
}

package me.seyoung.ecomerce.domain.order;

import lombok.Getter;

@Getter
public class OrderItem {
    private final Long productId;
    private final int quantity;
    private final long pricePerItem;

    public OrderItem(Long productId, int quantity, long pricePerItem) {
        if (quantity <= 0) throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        this.productId = productId;
        this.quantity = quantity;
        this.pricePerItem = pricePerItem;
    }

    public long calculatePrice() {
        return pricePerItem * quantity;
    }
}

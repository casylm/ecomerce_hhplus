package me.seyoung.ecomerce.domain.order;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private Long productId;
    private int quantity;
    private long pricePerItem;

    private OrderItem(Long productId, int quantity, long pricePerItem) {
        if (quantity <= 0) throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        this.productId = productId;
        this.quantity = quantity;
        this.pricePerItem = pricePerItem;
    }

    public static OrderItem create(Long productId, int quantity, long pricePerItem) {
        return new OrderItem(productId, quantity, pricePerItem);
    }

    public void assignId(Long id) {
        this.id = id;
    }

    public void assignOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public long calculatePrice() {
        return pricePerItem * quantity;
    }
}

package me.seyoung.ecomerce.domain.order;

import lombok.Getter;
import me.seyoung.ecomerce.domain.payment.Price;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class Order {

    private Long id;                        // 식별자 (Repository에서 부여)
    private final Long userId;
    private final List<OrderItem> items;

    private final Price totalPrice;         // 할인 전 총액
    private Price finalPrice;               // 할인 후 총액

    private OrderStatus status;
    private final LocalDateTime orderedAt;

    private Order(Long userId, List<OrderItem> items, Price totalPrice, Price finalPrice) {
        this.userId = userId;
        this.items = items;
        this.totalPrice = totalPrice;
        this.finalPrice = finalPrice;
        this.status = OrderStatus.CREATED;
        this.orderedAt = LocalDateTime.now();
    }

    // 생성 정적 팩토리 메서드
    public static Order create(Long userId, List<OrderItem> items, Price finalPrice) {
        Price totalPrice = calculateTotal(items);
        return new Order(userId, items, totalPrice, finalPrice);
    }

    private static Price calculateTotal(List<OrderItem> items) {
        Price total = new Price(0);
        for (OrderItem item : items) {
            Price itemPrice = new Price(item.calculatePrice());
            total = total.add(itemPrice);
        }
        return total;
    }

    // Repository에서 ID 할당 시 사용
    public void assignId(Long id) {
        this.id = id;
    }

    // 결제 완료 처리
    public void markAsPaid() {
        if (this.status == OrderStatus.PAID) {
            throw new IllegalStateException("이미 결제 완료된 주문입니다.");
        }
        if (this.status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("취소된 주문은 결제할 수 없습니다.");
        }
        this.status = OrderStatus.PAID;
    }

    // 주문 취소 (재고 복구 등은 Application Service에서 조율)
    public void cancel() {
        if (this.status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("이미 취소된 주문입니다.");
        }
        this.status = OrderStatus.CANCELLED;
    }

    // 주문 완료 처리
    public void complete() {
        if (this.status == OrderStatus.COMPLETED) {
            throw new IllegalStateException("이미 완료된 주문입니다.");
        }
        if (this.status != OrderStatus.PAID) {
            throw new IllegalStateException("결제 완료된 주문만 완료 처리할 수 있습니다.");
        }
        this.status = OrderStatus.COMPLETED;
    }
}
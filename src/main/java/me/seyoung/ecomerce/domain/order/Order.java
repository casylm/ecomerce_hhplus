package me.seyoung.ecomerce.domain.order;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.seyoung.ecomerce.domain.payment.Price;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;                        // 식별자 (Repository에서 부여)

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Transient
    private List<OrderItem> items = new ArrayList<>();

    @Column(name = "total_price", nullable = false)
    private Long totalPrice;         // 할인 전 총액

    @Column(name = "final_price", nullable = false)
    private Long finalPrice;               // 할인 후 총액

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "ordered_at", nullable = false, updatable = false)
    private LocalDateTime orderedAt;

    private Order(Long userId, List<OrderItem> items, Price totalPrice, Price finalPrice) {
        this.userId = userId;
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
        this.totalPrice = totalPrice.getAmount();
        this.finalPrice = finalPrice.getAmount();
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

    // items 설정 (Repository에서 조회 시 사용)
    public void setItems(List<OrderItem> items) {
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
    }

    // Price 객체 반환 메서드
    public Price getTotalPriceAsObject() {
        return new Price(this.totalPrice);
    }

    public Price getFinalPriceAsObject() {
        return new Price(this.finalPrice);
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
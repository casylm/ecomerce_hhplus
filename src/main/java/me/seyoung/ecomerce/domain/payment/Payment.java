package me.seyoung.ecomerce.domain.payment;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;                // 식별자 (InMemoryRepository에서 할당)

    @Column(name = "order_id", nullable = false)
    private Long orderId;     // 어떤 주문에 대한 결제인지

    @Column(name = "amount", nullable = false)
    private Long amount;     // 결제 금액 (최종 결제 금액)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;   // PENDING, SUCCESS, FAILED, CANCELLED

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "used_coupon_id")
    private Long usedCouponId;      // 사용한 쿠폰 ID (없으면 null)

    @Column(name = "used_point_amount")
    private Long usedPointAmount;   // 사용한 포인트 금액 (없으면 0)

    private Payment(Long orderId, Price amount, Long usedCouponId, Long usedPointAmount) {
        this.orderId = orderId;
        this.amount = amount.getAmount();
        this.usedCouponId = usedCouponId;
        this.usedPointAmount = usedPointAmount;
        this.status = PaymentStatus.PENDING;
    }

    public static Payment create(Long orderId, Price amount, Long usedCouponId, Long usedPointAmount) {
        return new Payment(orderId, amount, usedCouponId, usedPointAmount);
    }

    // ID 할당
    public void assignId(Long id) {
        this.id = id;
    }

    // Price 객체로 반환하는 메서드 추가
    public Price getAmountAsPrice() {
        return new Price(this.amount);
    }

    // 결제 성공 처리 (도메인 규칙 책임)
    public void complete() {
        if (this.status == PaymentStatus.SUCCESS) {
            throw new IllegalStateException("이미 결제된 주문입니다.");
        }
        this.status = PaymentStatus.SUCCESS;
        this.paidAt = LocalDateTime.now();
    }

    // 결제 실패 처리
    public void fail() {
        if (this.status == PaymentStatus.SUCCESS) {
            throw new IllegalStateException("이미 결제된 주문은 실패 처리할 수 없습니다.");
        }
        this.status = PaymentStatus.FAILED;
    }

    // 결제 취소 처리
    public void cancel() {
        if (this.status != PaymentStatus.SUCCESS) {
            throw new IllegalStateException("성공한 결제만 취소할 수 있습니다.");
        }
        this.status = PaymentStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
    }
}
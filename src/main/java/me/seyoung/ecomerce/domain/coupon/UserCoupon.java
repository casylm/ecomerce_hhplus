package me.seyoung.ecomerce.domain.coupon;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자가 발급받은 쿠폰
 * 사용자별 쿠폰 발급 및 사용 이력 관리
 */
@Entity
@Table(name = "user_coupons")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_coupon_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "coupon_id", nullable = false)
    private Long couponId;

    @Column(nullable = false)
    private boolean used; // 사용 여부

    @Column(name = "issued_at", nullable = false, updatable = false)
    private LocalDateTime issuedAt; // 발급 일시

    @Column(name = "used_at")
    private LocalDateTime usedAt; // 사용 일시

    // 도메인 로직을 위한 생성자
    public UserCoupon(Long userId, Long couponId) {
        this.userId = userId;
        this.couponId = couponId;
        this.used = false;
        this.issuedAt = LocalDateTime.now();
        this.usedAt = null;
    }

    // 인프라 계층에서 복원할 때 사용하는 생성자 (JPA 사용 시 불필요하지만 호환성 유지)
    public UserCoupon(Long id, Long userId, Long couponId, boolean used, LocalDateTime issuedAt, LocalDateTime usedAt) {
        this.id = id;
        this.userId = userId;
        this.couponId = couponId;
        this.used = used;
        this.issuedAt = issuedAt;
        this.usedAt = usedAt;
    }

    // 쿠폰 사용
    public void use() {
        if (this.used) {
            throw new IllegalStateException("이미 사용된 쿠폰입니다.");
        }
        this.used = true;
        this.usedAt = LocalDateTime.now();
    }

    // 쿠폰 사용 취소 (주문 취소 시)
    public void cancelUse() {
        if (!this.used) {
            throw new IllegalStateException("사용되지 않은 쿠폰은 취소할 수 없습니다.");
        }
        this.used = false;
        this.usedAt = null;
    }

    // 사용 가능 여부 확인
    public boolean isAvailable() {
        return !this.used;
    }

    // 특정 사용자의 쿠폰인지 확인
    public boolean isOwnedBy(Long userId) {
        return this.userId.equals(userId);
    }

    // ID 할당 (인프라 계층에서 사용)
    public void assignId(Long id) {
        this.id = id;
    }
}

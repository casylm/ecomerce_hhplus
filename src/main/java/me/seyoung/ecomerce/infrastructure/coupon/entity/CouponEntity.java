package me.seyoung.ecomerce.infrastructure.coupon.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 쿠폰 JPA 엔티티
 * 데이터베이스 매핑을 담당하는 인프라 계층 객체
 */
@Entity
@Table(name = "coupons")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "discount_amount", nullable = false)
    private int discountAmount;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;
}

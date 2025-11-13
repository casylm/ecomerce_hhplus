package me.seyoung.ecomerce.domain.coupon;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 쿠폰 마스터 정보 (순수 도메인 모델)
 * 발급 가능한 쿠폰의 기본 정보를 관리
 * JPA 어노테이션 없이 순수한 비즈니스 로직만 포함
 */
@Getter
public class Coupon {

    private Long id;
    private String name; // 쿠폰명
    private int discountAmount; // 할인 금액
    private int quantity; // 전체 발급 가능 수량
    private LocalDateTime issuedAt; // 쿠폰 생성 일시

    // 도메인 로직을 위한 생성자
    public Coupon(String name, int discountAmount, int quantity) {
        validateDiscountAmount(discountAmount);
        validateQuantity(quantity);

        this.name = name;
        this.discountAmount = discountAmount;
        this.quantity = quantity;
        this.issuedAt = LocalDateTime.now();
    }

    // 인프라 계층에서 복원할 때 사용하는 생성자
    public Coupon(Long id, String name, int discountAmount, int quantity, LocalDateTime issuedAt) {
        this.id = id;
        this.name = name;
        this.discountAmount = discountAmount;
        this.quantity = quantity;
        this.issuedAt = issuedAt;
    }

    // 발급 가능 여부 확인 (수량 체크는 별도 로직에서 처리)
    public boolean hasStock() {
        return this.quantity > 0;
    }

    // 재고 차감
    public void decreaseQuantity() {
        if (this.quantity <= 0) {
            throw new IllegalStateException("쿠폰 수량이 모두 소진되었습니다.");
        }
        this.quantity--;
    }

    // 검증 메서드
    private void validateDiscountAmount(int discountAmount) {
        if (discountAmount <= 0) {
            throw new IllegalArgumentException("할인 금액은 0보다 커야 합니다.");
        }
    }

    private void validateQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("발급 수량은 0 이상이어야 합니다.");
        }
    }

    // ID 할당 (인프라 계층에서 사용)
    public void assignId(Long id) {
        this.id = id;
    }
}

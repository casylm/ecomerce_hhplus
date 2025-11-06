package me.seyoung.ecomerce.domain.product;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private long price;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private String category;

    protected Product() {} // JPA 기본 생성자

    public Product(Long id, String name, long price, int stock, String category) {
        validateStock(stock);
        validatePrice(price);
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }

    // 재고 차감
    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("차감 수량은 0보다 커야 합니다.");
        }
        if (this.stock < quantity) {
            throw new IllegalStateException("재고가 부족합니다. 현재 재고: " + this.stock + ", 요청 수량: " + quantity);
        }
        this.stock -= quantity;
    }

    // 재고 증가 (반품, 취소 등)
    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("증가 수량은 0보다 커야 합니다.");
        }
        this.stock += quantity;
    }

    // 판매 가능 여부 확인
    public boolean isAvailable() {
        return this.stock > 0;
    }

    // 검증 메서드
    private void validateStock(int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("재고는 0 이상이어야 합니다.");
        }
    }

    private void validatePrice(long price) {
        if (price < 0) {
            throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
        }
    }
}

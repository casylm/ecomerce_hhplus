package me.seyoung.ecomerce.domain.point;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "points")
public class Point {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private long balance;   // 현재 포인트 잔액

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected Point() {} // JPA 기본 생성자

    public Point(Long userId, long balance) {
        if (balance < 0) throw new IllegalArgumentException("포인트는 음수가 될 수 없습니다.");
        this.userId = userId;
        this.balance = balance;
        this.updatedAt = LocalDateTime.now();
    }

    private static final long MAX_BALANCE_AMOUNT = 1_000_000L;

    // 포인트 충전
    public void charge(long amount) {
        validateAmount(amount);
        this.balance += amount;
        this.updatedAt = LocalDateTime.now();
    }

    // 포인트 사용
    public void use(long amount) {
        validateAmount(amount);
        if (this.balance < amount) {
            throw new IllegalStateException("포인트가 부족합니다.");
        }
        this.balance -= amount;
        this.updatedAt = LocalDateTime.now();
    }

    // 유효성 검사
    private void validateAmount(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("포인트 금액은 0보다 커야 합니다.");
        }

        if (amount+balance > MAX_BALANCE_AMOUNT) {
            throw new IllegalArgumentException("포인트 금액이 충전한도를 초과하였습니다.");
        }
    }

    public static enum PointStatus {
        CHARGE, USE
    }

}

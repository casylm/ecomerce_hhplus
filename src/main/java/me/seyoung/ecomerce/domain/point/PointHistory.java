package me.seyoung.ecomerce.domain.point;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "point_histories")
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PointStatus type;

    @Column(nullable = false)
    private long amount;

    @Column(nullable = false)
    private LocalDateTime occurredAt;

    protected PointHistory() {} // JPA 기본 생성자

    public PointHistory(Long userId, PointStatus type, long amount) {
        if (amount <= 0) throw new IllegalArgumentException("포인트 금액은 0보다 커야 합니다.");
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.occurredAt = LocalDateTime.now();
    }
}

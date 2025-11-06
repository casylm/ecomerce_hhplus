package me.seyoung.ecomerce.domain.user;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 사용자 엔티티
 */
@Entity
@Getter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;              // 사용자 식별자

    @Column(nullable = false)
    private String name;          // 사용자 이름

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;  // 가입일시

    protected User() {} // JPA 기본 생성자

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
        this.createdAt = LocalDateTime.now();
    }

    public User(String name) {
        this.name = name;
        this.createdAt = LocalDateTime.now();
    }
}

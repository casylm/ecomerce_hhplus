package me.seyoung.ecomerce.application.user;

import lombok.Getter;
import me.seyoung.ecomerce.domain.user.User;

import java.time.LocalDateTime;

@Getter
public class UserInfo {
    private final Long userId;
    private final String name;
    private final LocalDateTime createdAt;

    private UserInfo(Long userId, String name, LocalDateTime createdAt) {
        this.userId = userId;
        this.name = name;
        this.createdAt = createdAt;
    }

    public static UserInfo from(User user) {
        return new UserInfo(user.getId(), user.getName(), user.getCreatedAt());
    }
}

package me.seyoung.ecomerce.infrastructure.coupon.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponIssueEventPublisher {
    private final StringRedisTemplate redisTemplate;

    public void publish(Long userId, Long couponId) {
        String payload = userId + ":" + couponId;

        redisTemplate.opsForList()
                .leftPush("coupon:issue:queue", payload);
    }
}

package me.seyoung.ecomerce.application.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service("paymentRedisLockService")
@RequiredArgsConstructor
public class RedisLockService {

    private final StringRedisTemplate redisTemplate;

    public boolean acquireLock(String key, long ttlSeconds) {
        Boolean success = redisTemplate
                .opsForValue()
                .setIfAbsent(key, "LOCKED", Duration.ofSeconds(ttlSeconds));

        return Boolean.TRUE.equals(success);
    }

    public void releaseLock(String key) {
        redisTemplate.delete(key);
    }
}

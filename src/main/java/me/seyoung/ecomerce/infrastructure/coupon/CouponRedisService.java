package me.seyoung.ecomerce.infrastructure.coupon;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponRedisService {
    private final StringRedisTemplate redisTemplate;

    public boolean tryAcquire(Long userId, Long couponId) {

        String issuedKey = "coupon:issued:" + couponId;
        String stockKey = "coupon:stock:" + couponId;

        // 1. 중복 체크
        Long added = redisTemplate.opsForSet().add(issuedKey, String.valueOf(userId));
        if (added == 0L) return false;

        // 2. 재고 감소
        Long stock = redisTemplate.opsForValue().decrement(stockKey);
        if (stock < 0) {
            // 초과 발급 → 롤백
            redisTemplate.opsForValue().increment(stockKey);
            redisTemplate.opsForSet().remove(issuedKey, userId);
            return false;
        }

        return true;
    }
}

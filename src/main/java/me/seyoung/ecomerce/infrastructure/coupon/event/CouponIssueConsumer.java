package me.seyoung.ecomerce.infrastructure.coupon.event;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.application.coupon.IssueCouponUseCase;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponIssueConsumer {
    private final StringRedisTemplate redisTemplate;
    private final IssueCouponUseCase issueCouponUseCase;

    // 0.1초에 한 번씩 큐에서 읽어 처리
    @Scheduled(fixedDelay = 100)
    public void consume() {

        String payload = redisTemplate.opsForList()
                .rightPop("coupon:issue:queue");

        if (payload == null) {
            return;  // 큐가 비었음
        }

        String[] parts = payload.split(":");
        Long userId = Long.valueOf(parts[0]);
        Long couponId = Long.valueOf(parts[1]);

        try {
            issueCouponUseCase.execute(userId, couponId);  // 기존 DB 발급 로직 호출
        } catch (Exception e) {
            // 실패 시 재처리 or Dead Queue 전송 가능
            // redisTemplate.opsForList().leftPush("coupon:issue:dead", payload);
        }
    }
}

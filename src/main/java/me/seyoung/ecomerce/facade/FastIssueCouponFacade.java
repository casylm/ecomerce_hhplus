package me.seyoung.ecomerce.facade;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.application.coupon.CouponInfo;
import me.seyoung.ecomerce.application.coupon.IssueCouponUseCase;
import me.seyoung.ecomerce.infrastructure.coupon.CouponRedisService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FastIssueCouponFacade {
    private final CouponRedisService couponRedisService;
    private final IssueCouponUseCase issueCouponUseCase;

    public CouponInfo.CouponIssueResult issue(Long userId, Long couponId) {

        // 1. Redis 선착순 체크 (기존 코드 건드리지 않는 핵심)
        boolean ok = couponRedisService.tryAcquire(userId, couponId);

        if (!ok) {
            throw new IllegalStateException("선착순 마감 또는 중복 발급");
        }

        // 2. Redis에서 통과되면 기존 동기 코드 실행
        return issueCouponUseCase.execute(userId, couponId);
    }
}

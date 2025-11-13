package me.seyoung.ecomerce.infrastructure.coupon;

import me.seyoung.ecomerce.domain.coupon.Coupon;
import me.seyoung.ecomerce.domain.coupon.CouponRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 쿠폰 마스터 인메모리 Repository
 */
@Component
public class InMemoryCouponRepository implements CouponRepository {

    // 쿠폰 마스터 저장소
    private final Map<Long, Coupon> couponStore = new ConcurrentHashMap<>();
    private final AtomicLong couponIdGenerator = new AtomicLong(1);

    @Override
    public Optional<Coupon> findById(Long couponId) {
        return Optional.ofNullable(couponStore.get(couponId));
    }

    @Override
    public List<Coupon> findAll() {
        return new ArrayList<>(couponStore.values());
    }

    @Override
    public Coupon save(Coupon coupon) {
        // ID가 없으면 새로 생성
        if (coupon.getId() == null) {
            Long newId = couponIdGenerator.getAndIncrement();
            coupon.assignId(newId);
        }

        // 저장 (신규 또는 업데이트)
        couponStore.put(coupon.getId(), coupon);
        return coupon;
    }

    @Override
    public boolean hasStock(Long couponId) {
        return findById(couponId)
                .map(Coupon::hasStock)
                .orElse(false);
    }

    @Override
    public Optional<Coupon> findByIdWithLock(Long couponId) {
        // InMemory 환경에서는 락을 지원하지 않으므로 일반 findById 반환
        // 실제 락 기능은 JPA 구현체에서만 동작
        return findById(couponId);
    }
}

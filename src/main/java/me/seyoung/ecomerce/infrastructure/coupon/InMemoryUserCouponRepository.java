package me.seyoung.ecomerce.infrastructure.coupon;

import me.seyoung.ecomerce.domain.coupon.UserCoupon;
import me.seyoung.ecomerce.domain.coupon.UserCouponRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 사용자 쿠폰 인메모리 Repository
 */
@Component
public class InMemoryUserCouponRepository implements UserCouponRepository {

    // 사용자 쿠폰 저장소
    private final Map<Long, UserCoupon> userCouponStore = new ConcurrentHashMap<>();
    private final AtomicLong userCouponIdGenerator = new AtomicLong(1);

    @Override
    public Optional<UserCoupon> findById(Long userCouponId) {
        return Optional.ofNullable(userCouponStore.get(userCouponId));
    }

    @Override
    public Optional<UserCoupon> findByUserIdAndCouponId(Long userId, Long couponId) {
        return userCouponStore.values().stream()
                .filter(uc -> uc.getUserId().equals(userId) && uc.getCouponId().equals(couponId))
                .findFirst();
    }

    @Override
    public List<UserCoupon> findByUserId(Long userId) {
        return userCouponStore.values().stream()
                .filter(userCoupon -> userCoupon.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserCoupon> findAvailableByUserId(Long userId) {
        return userCouponStore.values().stream()
                .filter(userCoupon -> userCoupon.getUserId().equals(userId))
                .filter(UserCoupon::isAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public UserCoupon save(UserCoupon userCoupon) {
        // ID가 없으면 새로 생성
        if (userCoupon.getId() == null) {
            Long newId = userCouponIdGenerator.getAndIncrement();
            userCoupon.assignId(newId);
        }

        // 저장
        userCouponStore.put(userCoupon.getId(), userCoupon);
        return userCoupon;
    }

    @Override
    public boolean existsByUserIdAndCouponId(Long userId, Long couponId) {
        return userCouponStore.values().stream()
                .anyMatch(uc -> uc.getUserId().equals(userId) && uc.getCouponId().equals(couponId));
    }
}

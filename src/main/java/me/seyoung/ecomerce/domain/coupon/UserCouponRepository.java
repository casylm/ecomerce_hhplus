package me.seyoung.ecomerce.domain.coupon;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 쿠폰 Repository
 */
public interface UserCouponRepository {

    Optional<UserCoupon> findById(Long userCouponId);

    Optional<UserCoupon> findByUserIdAndCouponId(Long userId, Long couponId);

    List<UserCoupon> findByUserId(Long userId);

    List<UserCoupon> findAvailableByUserId(Long userId);

    UserCoupon save(UserCoupon userCoupon);

    int getRemainingQuantity(Long couponId);
}

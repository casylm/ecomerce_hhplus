package me.seyoung.ecomerce.infrastructure.coupon;

import me.seyoung.ecomerce.domain.coupon.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCouponJpaRepository extends JpaRepository<UserCoupon, Long> {

    Optional<UserCoupon> findByUserIdAndCouponId(Long userId, Long couponId);

    List<UserCoupon> findByUserId(Long userId);

    List<UserCoupon> findByUserIdAndUsed(Long userId, boolean used);
}

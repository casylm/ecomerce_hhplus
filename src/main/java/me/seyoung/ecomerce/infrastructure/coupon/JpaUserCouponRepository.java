package me.seyoung.ecomerce.infrastructure.coupon;

import me.seyoung.ecomerce.infrastructure.coupon.entity.UserCouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaUserCouponRepository extends JpaRepository<UserCouponEntity, Long> {

    Optional<UserCouponEntity> findByUserIdAndCouponId(Long userId, Long couponId);

    List<UserCouponEntity> findByUserId(Long userId);

    List<UserCouponEntity> findByUserIdAndUsed(Long userId, boolean used);

    @Query("SELECT c.quantity FROM CouponEntity c WHERE c.id = :couponId")
    int getRemainingQuantity(@Param("couponId") Long couponId);
}

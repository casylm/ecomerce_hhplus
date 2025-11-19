package me.seyoung.ecomerce.infrastructure.coupon;

import jakarta.persistence.LockModeType;
import me.seyoung.ecomerce.domain.coupon.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Coupon c WHERE c.id = :couponId")
    Optional<Coupon> findByIdWithLock(@Param("couponId") Long couponId);

    @Query("SELECT c.quantity FROM Coupon c WHERE c.id = :couponId")
    int getRemainingQuantity(@Param("couponId") Long couponId);
}

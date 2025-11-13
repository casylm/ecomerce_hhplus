package me.seyoung.ecomerce.infrastructure.coupon;

import jakarta.persistence.LockModeType;
import me.seyoung.ecomerce.infrastructure.coupon.entity.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * 쿠폰 JPA Repository
 * 비관적 락을 사용하여 동시성 제어
 */
public interface JpaCouponRepository extends JpaRepository<CouponEntity, Long> {

    /**
     * 비관적 쓰기 락을 사용하여 쿠폰 조회
     * SELECT ... FOR UPDATE 쿼리 실행
     *
     * @param couponId 쿠폰 ID
     * @return 쿠폰 엔티티 Optional
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CouponEntity c WHERE c.id = :couponId")
    Optional<CouponEntity> findByIdWithLock(@Param("couponId") Long couponId);
}

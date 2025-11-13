package me.seyoung.ecomerce.domain.coupon;

import java.util.List;
import java.util.Optional;

/**
 * 쿠폰 마스터 정보 Repository
 */
public interface CouponRepository {

    /**
     * 쿠폰 ID로 조회
     * @param couponId 쿠폰 ID
     * @return 쿠폰 Optional
     */
    Optional<Coupon> findById(Long couponId);

    /**
     * 모든 쿠폰 조회
     * @return 쿠폰 목록
     */
    List<Coupon> findAll();

    /**
     * 쿠폰 저장
     * @param coupon 저장할 쿠폰
     * @return 저장된 쿠폰
     */
    Coupon save(Coupon coupon);

    /**
     * 쿠폰 발급 가능 여부 확인
     * @param couponId 쿠폰 ID
     * @return 발급 가능 여부
     */
    boolean hasStock(Long couponId);

    /**
     * 비관적 락을 사용하여 쿠폰 조회
     * 동시성 제어를 위해 사용
     * @param couponId 쿠폰 ID
     * @return 쿠폰 Optional
     */
    Optional<Coupon> findByIdWithLock(Long couponId);
}

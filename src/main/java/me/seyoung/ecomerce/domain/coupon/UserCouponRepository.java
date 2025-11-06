package me.seyoung.ecomerce.domain.coupon;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 쿠폰 Repository
 */
public interface UserCouponRepository {

    /**
     * 사용자 쿠폰 ID로 조회
     * @param userCouponId 사용자 쿠폰 ID
     * @return 사용자 쿠폰 Optional
     */
    Optional<UserCoupon> findById(Long userCouponId);

    /**
     * 사용자 ID와 쿠폰 ID로 조회
     * @param userId 사용자 ID
     * @param couponId 쿠폰 ID
     * @return 사용자 쿠폰 Optional
     */
    Optional<UserCoupon> findByUserIdAndCouponId(Long userId, Long couponId);

    /**
     * 사용자가 보유한 모든 쿠폰 조회
     * @param userId 사용자 ID
     * @return 사용자 쿠폰 목록
     */
    List<UserCoupon> findByUserId(Long userId);

    /**
     * 사용자가 보유한 사용 가능한 쿠폰 조회
     * @param userId 사용자 ID
     * @return 사용 가능한 쿠폰 목록
     */
    List<UserCoupon> findAvailableByUserId(Long userId);

    /**
     * 사용자 쿠폰 저장
     * @param userCoupon 저장할 사용자 쿠폰
     * @return 저장된 사용자 쿠폰
     */
    UserCoupon save(UserCoupon userCoupon);

    /**
     * 사용자가 특정 쿠폰을 이미 발급받았는지 확인
     * @param userId 사용자 ID
     * @param couponId 쿠폰 ID
     * @return 발급 여부
     */
    boolean existsByUserIdAndCouponId(Long userId, Long couponId);
}

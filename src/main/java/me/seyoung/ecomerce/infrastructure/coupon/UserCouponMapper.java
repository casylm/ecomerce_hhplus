package me.seyoung.ecomerce.infrastructure.coupon;

import me.seyoung.ecomerce.domain.coupon.UserCoupon;
import me.seyoung.ecomerce.infrastructure.coupon.entity.UserCouponEntity;
import org.springframework.stereotype.Component;

/**
 * 사용자 쿠폰 도메인 모델과 엔티티 간 변환을 담당하는 매퍼
 */
@Component
public class UserCouponMapper {

    /**
     * 엔티티를 도메인 모델로 변환
     */
    public UserCoupon toDomain(UserCouponEntity entity) {
        if (entity == null) {
            return null;
        }

        return new UserCoupon(
            entity.getId(),
            entity.getUserId(),
            entity.getCouponId(),
            entity.isUsed(),
            entity.getIssuedAt(),
            entity.getUsedAt()
        );
    }

    /**
     * 도메인 모델을 엔티티로 변환
     */
    public UserCouponEntity toEntity(UserCoupon domain) {
        if (domain == null) {
            return null;
        }

        return UserCouponEntity.builder()
            .id(domain.getId())
            .userId(domain.getUserId())
            .couponId(domain.getCouponId())
            .used(domain.isUsed())
            .issuedAt(domain.getIssuedAt())
            .usedAt(domain.getUsedAt())
            .build();
    }
}

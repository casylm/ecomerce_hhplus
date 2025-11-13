package me.seyoung.ecomerce.infrastructure.coupon;

import me.seyoung.ecomerce.domain.coupon.Coupon;
import me.seyoung.ecomerce.infrastructure.coupon.entity.CouponEntity;
import org.springframework.stereotype.Component;

/**
 * 쿠폰 도메인 모델과 엔티티 간 변환을 담당하는 매퍼
 */
@Component
public class CouponMapper {

    /**
     * 엔티티를 도메인 모델로 변환
     */
    public Coupon toDomain(CouponEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Coupon(
            entity.getId(),
            entity.getName(),
            entity.getDiscountAmount(),
            entity.getQuantity(),
            entity.getIssuedAt()
        );
    }

    /**
     * 도메인 모델을 엔티티로 변환
     */
    public CouponEntity toEntity(Coupon domain) {
        if (domain == null) {
            return null;
        }

        return CouponEntity.builder()
            .id(domain.getId())
            .name(domain.getName())
            .discountAmount(domain.getDiscountAmount())
            .quantity(domain.getQuantity())
            .issuedAt(domain.getIssuedAt())
            .build();
    }

    /**
     * 도메인 모델을 기존 엔티티에 반영 (업데이트 시 사용)
     */
    public CouponEntity updateEntity(Coupon domain, CouponEntity entity) {
        return CouponEntity.builder()
            .id(entity.getId())  // ID는 기존 엔티티 것 유지
            .name(domain.getName())
            .discountAmount(domain.getDiscountAmount())
            .quantity(domain.getQuantity())
            .issuedAt(domain.getIssuedAt())
            .build();
    }
}

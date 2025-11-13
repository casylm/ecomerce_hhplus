package me.seyoung.ecomerce.infrastructure.coupon;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.domain.coupon.Coupon;
import me.seyoung.ecomerce.domain.coupon.CouponRepository;
import me.seyoung.ecomerce.infrastructure.coupon.entity.CouponEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 쿠폰 Repository 구현체
 * JPA를 사용하여 동시성 제어
 * 도메인 모델과 엔티티 간 변환을 담당
 */
@Repository
@Primary
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {

    private final JpaCouponRepository jpaCouponRepository;
    private final CouponMapper couponMapper;

    @Override
    public Optional<Coupon> findById(Long couponId) {
        return jpaCouponRepository.findById(couponId)
                .map(couponMapper::toDomain);
    }

    @Override
    public List<Coupon> findAll() {
        return jpaCouponRepository.findAll()
                .stream()
                .map(couponMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Coupon save(Coupon coupon) {
        CouponEntity entity = couponMapper.toEntity(coupon);
        CouponEntity savedEntity = jpaCouponRepository.save(entity);
        return couponMapper.toDomain(savedEntity);
    }

    @Override
    public boolean hasStock(Long couponId) {
        return findById(couponId)
                .map(Coupon::hasStock)
                .orElse(false);
    }

    @Override
    public Optional<Coupon> findByIdWithLock(Long couponId) {
        return jpaCouponRepository.findByIdWithLock(couponId)
                .map(couponMapper::toDomain);
    }
}

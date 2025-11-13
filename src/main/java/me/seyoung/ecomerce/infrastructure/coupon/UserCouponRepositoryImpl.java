package me.seyoung.ecomerce.infrastructure.coupon;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.domain.coupon.UserCoupon;
import me.seyoung.ecomerce.domain.coupon.UserCouponRepository;
import me.seyoung.ecomerce.infrastructure.coupon.entity.UserCouponEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Primary
@RequiredArgsConstructor
public class UserCouponRepositoryImpl implements UserCouponRepository {

    private final JpaUserCouponRepository jpaUserCouponRepository;
    private final UserCouponMapper userCouponMapper;

    @Override
    public Optional<UserCoupon> findById(Long userCouponId) {
        return jpaUserCouponRepository.findById(userCouponId)
                .map(userCouponMapper::toDomain);
    }

    @Override
    public Optional<UserCoupon> findByUserIdAndCouponId(Long userId, Long couponId) {
        return jpaUserCouponRepository.findByUserIdAndCouponId(userId, couponId)
                .map(userCouponMapper::toDomain);
    }

    @Override
    public List<UserCoupon> findByUserId(Long userId) {
        return jpaUserCouponRepository.findByUserId(userId)
                .stream()
                .map(userCouponMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserCoupon> findAvailableByUserId(Long userId) {
        return jpaUserCouponRepository.findByUserIdAndUsed(userId, false)
                .stream()
                .map(userCouponMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public UserCoupon save(UserCoupon userCoupon) {
        UserCouponEntity entity = userCouponMapper.toEntity(userCoupon);
        UserCouponEntity savedEntity = jpaUserCouponRepository.save(entity);
        return userCouponMapper.toDomain(savedEntity);
    }

    @Override
    public int getRemainingQuantity(Long couponId) {
        return jpaUserCouponRepository.getRemainingQuantity(couponId);
    }


}

package me.seyoung.ecomerce.domain.coupon;

import java.util.List;
import java.util.Optional;

public interface CouponRepository {

    Optional<Coupon> findById(Long couponId);

    List<Coupon> findAll();

    Coupon save(Coupon coupon);

    boolean hasStock(Long couponId);

    Optional<Coupon> findByIdWithLock(Long couponId);
}

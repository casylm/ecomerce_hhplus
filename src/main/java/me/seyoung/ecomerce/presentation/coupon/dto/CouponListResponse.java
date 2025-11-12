package me.seyoung.ecomerce.presentation.coupon.dto;

import me.seyoung.ecomerce.application.coupon.CouponInfo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record CouponListResponse(
        List<CouponDto> coupons
) {
    public static CouponListResponse from(CouponInfo.Coupons couponInfo) {
        List<CouponDto> coupons = new ArrayList<>();

        for (CouponInfo.CouponDetailInfo detail : couponInfo.getCoupons()) {
            coupons.add(CouponDto.from(detail));
        }

        return new CouponListResponse(coupons);
    }

    public record CouponDto(
            Long couponId,
            Long userCouponId,
            String name,
            int discountAmount,
            boolean used,
            LocalDateTime issuedAt,
            LocalDateTime usedAt
    ) {
        public static CouponDto from(CouponInfo.CouponDetailInfo detail) {
            return new CouponDto(
                    detail.getCouponId(),
                    detail.getUserCouponId(),
                    detail.getName(),
                    detail.getDiscountAmount(),
                    detail.isUsed(),
                    detail.getIssuedAt(),
                    detail.getUsedAt()
            );
        }
    }
}

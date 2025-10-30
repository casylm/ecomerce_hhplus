package me.seyoung.ecomerce.api.dto.coupon;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "쿠폰 사용 응답 DTO")
public record CouponUseResponse(
        @Schema(description = "쿠폰 ID", example = "C001") String couponId,
        @Schema(description = "할인 금액", example = "5000") Integer discountAmount,
        @Schema(description = "원래 주문 금액", example = "50000") Integer originalAmount,
        @Schema(description = "할인 후 금액", example = "45000") Integer finalAmount,
        @Schema(description = "사용 일시", example = "2025-10-30T14:30:00") LocalDateTime usedAt
) {}

package me.seyoung.ecomerce.api.dto.coupon;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "쿠폰 정보 응답 DTO")
public record CouponResponse(
        @Schema(description = "쿠폰 ID", example = "C001") String couponId,
        @Schema(description = "쿠폰명", example = "신규 회원 할인 쿠폰") String couponName,
        @Schema(description = "할인 금액", example = "5000") Integer discountAmount,
        @Schema(description = "쿠폰 상태", example = "AVAILABLE", allowableValues = {"AVAILABLE", "USED", "EXPIRED"}) String status,
        @Schema(description = "발급 일시", example = "2025-10-30T10:00:00") LocalDateTime issuedAt,
        @Schema(description = "사용 일시", example = "2025-10-31T14:30:00", nullable = true) LocalDateTime usedAt,
        @Schema(description = "만료 일시", example = "2025-11-30T23:59:59") LocalDateTime expiresAt
) {}

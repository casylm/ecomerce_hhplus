package me.seyoung.ecomerce.api.dto.coupon;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "쿠폰 발급 응답 DTO")
public record CouponIssueResponse(
        @Schema(description = "쿠폰 ID", example = "C001") String couponId,
        @Schema(description = "사용자 ID", example = "1") Long userId,
        @Schema(description = "쿠폰명", example = "신규 회원 할인 쿠폰") String couponName,
        @Schema(description = "할인 금액", example = "5000") Integer discountAmount,
        @Schema(description = "발급 일시", example = "2025-10-30T10:00:00") LocalDateTime issuedAt,
        @Schema(description = "만료 일시", example = "2025-11-30T23:59:59") LocalDateTime expiresAt
) {}

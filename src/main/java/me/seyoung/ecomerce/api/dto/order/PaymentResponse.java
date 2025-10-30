package me.seyoung.ecomerce.api.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "결제 응답 DTO")
public record PaymentResponse(
        @Schema(description = "주문 ID", example = "ORD001") String orderId,
        @Schema(description = "사용자 ID", example = "1") Long userId,
        @Schema(description = "원래 주문 금액", example = "3000000") Integer originalAmount,
        @Schema(description = "할인 금액", example = "5000") Integer discountAmount,
        @Schema(description = "최종 결제 금액", example = "2995000") Integer finalAmount,
        @Schema(description = "결제 후 잔액", example = "5000") Long remainingBalance,
        @Schema(description = "주문 상태", example = "PAID") String status,
        @Schema(description = "결제 일시", example = "2025-10-30T15:05:00") LocalDateTime paidAt
) {}

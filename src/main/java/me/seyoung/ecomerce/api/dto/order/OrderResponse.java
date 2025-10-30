package me.seyoung.ecomerce.api.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "주문 조회 응답 DTO")
public record OrderResponse(
        @Schema(description = "주문 ID", example = "ORD001") String orderId,
        @Schema(description = "사용자 ID", example = "1") Long userId,
        @Schema(description = "주문 상품 목록") List<OrderItemResponse> items,
        @Schema(description = "총 주문 금액", example = "3000000") Integer totalAmount,
        @Schema(description = "할인 금액", example = "5000") Integer discountAmount,
        @Schema(description = "최종 결제 금액", example = "2995000") Integer finalAmount,
        @Schema(description = "주문 상태", example = "PAID", allowableValues = {"PENDING", "PAID", "CANCELLED"}) String status,
        @Schema(description = "주문 일시", example = "2025-10-30T15:00:00") LocalDateTime orderedAt,
        @Schema(description = "결제 일시", example = "2025-10-30T15:05:00", nullable = true) LocalDateTime paidAt
) {}

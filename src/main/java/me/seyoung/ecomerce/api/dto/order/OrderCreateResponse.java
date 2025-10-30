package me.seyoung.ecomerce.api.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "주문 생성 응답 DTO")
public record OrderCreateResponse(
        @Schema(description = "주문 ID", example = "ORD001") String orderId,
        @Schema(description = "사용자 ID", example = "1") Long userId,
        @Schema(description = "주문 상품 목록") List<OrderItemResponse> items,
        @Schema(description = "총 주문 금액", example = "3000000") Integer totalAmount,
        @Schema(description = "주문 상태", example = "PENDING", allowableValues = {"PENDING", "PAID", "CANCELLED"}) String status,
        @Schema(description = "주문 일시", example = "2025-10-30T15:00:00") LocalDateTime orderedAt
) {}

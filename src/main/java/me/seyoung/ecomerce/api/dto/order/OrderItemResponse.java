package me.seyoung.ecomerce.api.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "주문 상품 항목 응답 DTO")
public record OrderItemResponse(
        @Schema(description = "주문 상품 ID", example = "1") Long orderItemId,
        @Schema(description = "상품 ID", example = "P001") String productId,
        @Schema(description = "상품명", example = "노트북") String productName,
        @Schema(description = "단가", example = "1500000") Integer unitPrice,
        @Schema(description = "주문 수량", example = "2") Integer quantity,
        @Schema(description = "소계", example = "3000000") Integer subtotal
) {}

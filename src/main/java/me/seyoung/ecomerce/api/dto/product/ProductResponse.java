package me.seyoung.ecomerce.api.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상품 정보 응답 DTO")
public record ProductResponse(
        @Schema(description = "상품 ID", example = "P001") String productId,
        @Schema(description = "상품 이름", example = "노트북") String name,
        @Schema(description = "가격", example = "1500000") int price,
        @Schema(description = "재고 수량", example = "10") int stock,
        @Schema(description = "카테고리", example = "electronics") String category
) {}

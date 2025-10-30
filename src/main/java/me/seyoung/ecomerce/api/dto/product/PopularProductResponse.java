package me.seyoung.ecomerce.api.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "인기 상품 정보 응답 DTO")
public record PopularProductResponse(
        @Schema(description = "상품 ID", example = "P001") String productId,
        @Schema(description = "상품명", example = "노트북") String name,
        @Schema(description = "가격", example = "1500000") int price,
        @Schema(description = "재고 수량", example = "10") int stock,
        @Schema(description = "카테고리", example = "electronics") String category,
        @Schema(description = "판매량 (최근 3일)", example = "150") int salesCount
) {}

package me.seyoung.ecomerce.api.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상품 상세 정보 응답 DTO")
public record ProductDetailResponse(
        @Schema(description = "상품 ID", example = "P001") String productId,
        @Schema(description = "상품명", example = "노트북") String name,
        @Schema(description = "상품 설명", example = "고성능 업무용 노트북") String description,
        @Schema(description = "가격", example = "1500000") int price,
        @Schema(description = "재고 수량", example = "10") int stock,
        @Schema(description = "카테고리", example = "electronics") String category,
        @Schema(description = "품절 여부", example = "false") boolean soldOut
) {}

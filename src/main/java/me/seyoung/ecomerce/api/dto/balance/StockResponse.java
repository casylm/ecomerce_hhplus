package me.seyoung.ecomerce.api.dto.balance;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상품 재고 정보 응답 DTO")
public record StockResponse(
        @Schema(description = "상품 ID", example = "P001") String productId,
        @Schema(description = "재고 수량", example = "10") int stock,
        @Schema(description = "품절 여부", example = "false") boolean soldOut
) {}

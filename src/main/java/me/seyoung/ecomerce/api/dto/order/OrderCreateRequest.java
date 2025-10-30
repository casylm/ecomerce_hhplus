package me.seyoung.ecomerce.api.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "주문 생성 요청 DTO")
public record OrderCreateRequest(
        @Schema(description = "사용자 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "사용자 ID는 필수입니다")
        Long userId,

        @Schema(description = "주문 상품 목록", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = "주문 상품 목록은 필수입니다")
        @Valid
        List<OrderItemRequest> items
) {}

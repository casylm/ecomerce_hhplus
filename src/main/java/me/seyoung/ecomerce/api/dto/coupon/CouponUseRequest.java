package me.seyoung.ecomerce.api.dto.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "쿠폰 사용 요청 DTO")
public record CouponUseRequest(
        @Schema(description = "사용자 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "사용자 ID는 필수입니다")
        Long userId,

        @Schema(description = "주문 ID", example = "ORD001", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "주문 ID는 필수입니다")
        String orderId,

        @Schema(description = "주문 금액", example = "50000", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "주문 금액은 필수입니다")
        Integer orderAmount
) {}

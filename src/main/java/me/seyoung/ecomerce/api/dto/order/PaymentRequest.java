package me.seyoung.ecomerce.api.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "결제 요청 DTO")
public record PaymentRequest(
        @Schema(description = "사용자 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "사용자 ID는 필수입니다")
        Long userId,

        @Schema(description = "사용자 쿠폰 ID (선택)", example = "1001")
        Long userCouponId
) {}

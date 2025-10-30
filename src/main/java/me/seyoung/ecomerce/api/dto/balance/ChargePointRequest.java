package me.seyoung.ecomerce.api.dto.balance;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "잔액 충전 요청 DTO")
public record ChargePointRequest(
        @Schema(description = "사용자 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "사용자 ID는 필수입니다")
        Long userId,

        @Schema(description = "충전 금액 (양수)", example = "10000", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "충전 금액은 필수입니다")
        @Min(value = 1, message = "충전 금액은 양수여야 합니다")
        Long amount
) {}

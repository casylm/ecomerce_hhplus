package me.seyoung.ecomerce.api.dto.balance;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "잔액 충전 응답 DTO")
public record ChargePointResponse(
        @Schema(description = "사용자 ID", example = "1") Long userId,
        @Schema(description = "충전 후 잔액", example = "60000") Long balance,
        @Schema(description = "충전 금액", example = "10000") Long chargedAmount
) {}

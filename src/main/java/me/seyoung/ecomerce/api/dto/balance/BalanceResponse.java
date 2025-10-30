package me.seyoung.ecomerce.api.dto.balance;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "잔액 조회 응답 DTO")
public record BalanceResponse (
        @Schema(description = "사용자 ID", example = "1") Long userId,
        @Schema(description = "현재 잔액", example = "50000") Long balance
) {}

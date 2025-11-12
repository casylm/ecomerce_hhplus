package me.seyoung.ecomerce.presentation.point.dto;

public record ChargePointRequest(
        Long userId,
        long amount
) {
}

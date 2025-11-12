package me.seyoung.ecomerce.presentation.point.dto;

public record UsePointRequest(
        Long userId,
        long amount
) {
}

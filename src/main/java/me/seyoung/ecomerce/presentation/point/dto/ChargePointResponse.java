package me.seyoung.ecomerce.presentation.point.dto;

import me.seyoung.ecomerce.application.point.PointInfo;

public record ChargePointResponse(
        Long userId,
        Long currentPoint
) {
    public static ChargePointResponse from(PointInfo.ChargePointResponse pointInfo) {
        return new ChargePointResponse(
                pointInfo.getUserId(),
                pointInfo.getCurrentPoint()
        );
    }
}

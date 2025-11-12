package me.seyoung.ecomerce.presentation.point.dto;

import me.seyoung.ecomerce.application.point.PointInfo;

public record UsePointResponse(
        Long userId,
        Long currentPoint
) {
    public static UsePointResponse from(PointInfo.UsePointResponse pointInfo) {
        return new UsePointResponse(
                pointInfo.getUserId(),
                pointInfo.getCurrentPoint()
        );
    }
}

package me.seyoung.ecomerce.presentation.point.dto;

import me.seyoung.ecomerce.application.point.PointInfo;

public record GetPointResponse(
        Long userId,
        Long point
) {
    public static GetPointResponse from(PointInfo.GetPointResponse pointInfo) {
        return new GetPointResponse(
                pointInfo.getUserId(),
                pointInfo.getPoint()
        );
    }
}

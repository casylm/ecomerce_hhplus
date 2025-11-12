package me.seyoung.ecomerce.application.point;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.seyoung.ecomerce.domain.point.Point;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointInfo {

    /**
     * 포인트 충전 응답
     */
    @Getter
    @Builder
    public static class ChargePointResponse {
        private final Long userId;
        private final Long currentPoint;

        public static ChargePointResponse of(Long userId, Long currentPoint) {
            return ChargePointResponse.builder()
                    .userId(userId)
                    .currentPoint(currentPoint)
                    .build();
        }
    }

    /**
     * 포인트 사용 응답
     */
    @Getter
    @Builder
    public static class UsePointResponse {
        private final Long userId;
        private final Long currentPoint;

        public static UsePointResponse of(Long userId, Long currentPoint) {
            return UsePointResponse.builder()
                    .userId(userId)
                    .currentPoint(currentPoint)
                    .build();
        }
    }

    /**
     * 포인트 조회 응답
     */
    @Getter
    @Builder
    public static class GetPointResponse {
        private final Long userId;
        private final Long point;

        public static GetPointResponse of(Long userId, Long point) {
            return GetPointResponse.builder()
                    .userId(userId)
                    .point(point)
                    .build();
        }
    }
}

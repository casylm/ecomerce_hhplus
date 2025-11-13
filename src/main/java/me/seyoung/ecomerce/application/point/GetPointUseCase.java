package me.seyoung.ecomerce.application.point;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.domain.point.Point;
import me.seyoung.ecomerce.domain.point.PointRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetPointUseCase {
    private final PointRepository pointRepository;

    public PointInfo.GetPointResponse execute(Long userId, Long amount) {
        // 포인트 조회
        Point point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 포인트를 찾을 수 없습니다."));

        long getPoint = point.getBalance();

        // 4. 응답 반환
        return PointInfo.GetPointResponse.of(userId, getPoint);
    }
}

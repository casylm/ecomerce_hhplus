package me.seyoung.ecomerce.application.point;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.domain.point.Point;
import me.seyoung.ecomerce.domain.point.PointHistory;
import me.seyoung.ecomerce.domain.point.PointRepository;
import me.seyoung.ecomerce.domain.point.PointStatus;
import me.seyoung.ecomerce.domain.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChargePointUseCase {
    private final PointRepository pointRepository;
    private final UserRepository userRepository;

    public PointInfo.ChargePointResponse execute(Long userId, Long amount) {
        // 1. 사용자 검증
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Point point = pointRepository.findByUserId(userId)
                .orElseGet(() -> new Point(userId, 0L));

        // 2. 포인트 충전 (Repository에서 검증 및 충전 처리)
        point.charge(amount); // 여기서 도메인 로직 수행

        pointRepository.save(point);

        // 3. 충전 이력 기록
        PointHistory history = new PointHistory(userId, PointStatus.CHARGE, amount);

        // 4. 응답 반환
        return PointInfo.ChargePointResponse.of(userId, point.getBalance());
    }
}

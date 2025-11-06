package me.seyoung.ecomerce.application.point;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.domain.point.Point;
import me.seyoung.ecomerce.domain.point.PointHistory;
import me.seyoung.ecomerce.domain.point.PointStatus;
import me.seyoung.ecomerce.infrastructure.point.InMemoryPointRepository;
import me.seyoung.ecomerce.infrastructure.user.InMemoryUserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsePointUseCase {
    private final InMemoryPointRepository pointRepository;
    private final InMemoryUserRepository userRepository;

    // 포인트 사용
    public PointInfo.UsePointResponse excute(Long userId, Long amount) {
        // 사용자 검증
        userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Point usedPoint = pointRepository.use(userId, amount);

        PointHistory history = new PointHistory(userId, PointStatus.USE, amount);

        return PointInfo.UsePointResponse.of(userId, usedPoint.getBalance());
    }
}

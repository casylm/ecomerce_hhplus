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
public class UsePointUseCase {
    private final PointRepository pointRepository;
    private final UserRepository userRepository;

    // 포인트 사용
    public PointInfo.UsePointResponse execute(Long userId, Long amount) {
        // 사용자 검증
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Point point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 포인트를 찾을 수 없습니다."));

        point.use(amount);
        pointRepository.save(point);

        PointHistory history = new PointHistory(userId, PointStatus.USE, amount);

        return PointInfo.UsePointResponse.of(userId, point.getBalance());
    }
}

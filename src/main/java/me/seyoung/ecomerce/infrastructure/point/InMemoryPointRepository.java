package me.seyoung.ecomerce.infrastructure.point;

import me.seyoung.ecomerce.domain.point.Point;
import me.seyoung.ecomerce.domain.point.PointRepository;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryPointRepository implements PointRepository {

    // 사용자별 포인트를 저장할 메모리 Map
    private final Map<Long, Point> store = new ConcurrentHashMap<>();

    @Override
    public Optional<Point> findByUserId(Long userId) {
        return Optional.ofNullable(store.get(userId));
    }

    @Override
    public Point charge(Long userId, long amount) {
        // 기존 포인트가 없으면 새로 생성
        Point point = store.getOrDefault(userId, new Point(userId, 0L));
        point.charge(amount);
        store.put(userId, point);

        return point;
    }

    @Override
    public Point use(Long userId, long amount) {
        Point point = store.get(userId);

        if (point == null) {
            throw new IllegalStateException("해당 사용자의 포인트가 존재하지 않습니다.");
        }

        point.use(amount);
        store.put(userId, point);

        return point;
    }

    @Override
    public long getBalance(Long userId) {
        Point point = store.get(userId);
        return point != null ? point.getBalance() : 0L;
    }

    @Override
    public Point save(Point point) {
        store.put(point.getUserId(), point);
        return point;
    }
}

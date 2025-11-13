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
    public Point save(Point point) {
        store.put(point.getUserId(), point);
        return point;
    }
}

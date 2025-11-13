package me.seyoung.ecomerce.infrastructure.point;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.domain.point.Point;
import me.seyoung.ecomerce.domain.point.PointRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Primary
@Repository
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepository {

    private final PointJpaRepository pointJpaRepository;

    @Override
    // jpa 값 return
    public Optional<Point> findByUserId(Long userId) {
        return pointJpaRepository.findByUserId(userId);
    }

    @Override
    public Point save(Point point) {
        return pointJpaRepository.save(point);
    }
}

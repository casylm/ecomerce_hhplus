package me.seyoung.ecomerce.domain.point;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointRepository {
    Optional<Point> findByUserId(Long userId);
    Point save(Point point);
}

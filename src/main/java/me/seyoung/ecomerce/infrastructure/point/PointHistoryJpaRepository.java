package me.seyoung.ecomerce.infrastructure.point;

import me.seyoung.ecomerce.domain.point.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointHistoryJpaRepository extends JpaRepository<PointHistory, Long> {
}

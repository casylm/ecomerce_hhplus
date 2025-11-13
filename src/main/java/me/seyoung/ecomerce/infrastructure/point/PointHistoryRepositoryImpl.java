package me.seyoung.ecomerce.infrastructure.point;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.domain.point.PointHistory;
import me.seyoung.ecomerce.domain.point.PointHistoryRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PointHistoryRepositoryImpl implements PointHistoryRepository {

    private final PointHistoryJpaRepository pointHistoryJpaRepository;

    @Override
    public PointHistory save(PointHistory pointHistory) {
        return pointHistoryJpaRepository.save(pointHistory);
    }
}

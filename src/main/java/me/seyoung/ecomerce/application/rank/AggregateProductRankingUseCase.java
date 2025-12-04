package me.seyoung.ecomerce.application.rank;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.domain.rank.ProductRankingRepository;
import me.seyoung.ecomerce.domain.rank.RankingPeriod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AggregateProductRankingUseCase {
    private final ProductRankingRepository productRankingRepository;

    public void aggregate(RankingPeriod period) {
        productRankingRepository.aggregate(period);
    }
}

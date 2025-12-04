package me.seyoung.ecomerce.application.rank;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.domain.rank.ProductRankingItem;
import me.seyoung.ecomerce.domain.rank.ProductRankingRepository;
import me.seyoung.ecomerce.domain.rank.RankingPeriod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetProductRankingUseCase {
    private final ProductRankingRepository productRankingRepository;

    public List<ProductRankingItem> execute(RankingPeriod period, int limit) {
        return productRankingRepository.findTopN(period, limit);
    }
}

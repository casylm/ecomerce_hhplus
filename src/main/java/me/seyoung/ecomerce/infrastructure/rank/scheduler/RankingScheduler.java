package me.seyoung.ecomerce.infrastructure.rank.scheduler;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.application.rank.AggregateProductRankingUseCase;
import me.seyoung.ecomerce.domain.rank.RankingPeriod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RankingScheduler {
    private final AggregateProductRankingUseCase aggregateProductRankingUseCase;

    // 5분마다 3일 / 7일 랭킹 집계
    @Scheduled(cron = "0 */5 * * * *")
    public void aggregateRanking() {
        aggregateProductRankingUseCase.aggregate(RankingPeriod.THREE_DAYS);
        aggregateProductRankingUseCase.aggregate(RankingPeriod.SEVEN_DAYS);
    }
}

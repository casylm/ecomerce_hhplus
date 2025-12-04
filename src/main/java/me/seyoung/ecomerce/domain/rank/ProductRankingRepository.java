package me.seyoung.ecomerce.domain.rank;

import java.util.List;

public interface ProductRankingRepository {

    /**
     * 오늘 날짜의 일별 랭킹에서 해당 상품의 판매량 증가
     */
    void increaseSales(Long productId, long quantity);

    /**
     * 주어진 기간(3일 / 7일 등)에 해당하는 집계 랭킹 생성
     */
    void aggregate(RankingPeriod period);

    /**
     * 특정 기간 기준으로 TOP N 랭킹 조회
     */
    List<ProductRankingItem> findTopN(RankingPeriod period, int limit);
}

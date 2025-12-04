package me.seyoung.ecomerce.infrastructure.rank;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.domain.rank.ProductRankingItem;
import me.seyoung.ecomerce.domain.rank.ProductRankingRepository;
import me.seyoung.ecomerce.domain.rank.RankingPeriod;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

@Repository
@RequiredArgsConstructor
public class RedisProductRankingRepository implements ProductRankingRepository {

    private final StringRedisTemplate redisTemplate;

    private static final String DAILY_KEY_PREFIX = "ranking:sales:daily:";
    private static final String KEY_3_DAYS = "ranking:sales:3days";
    private static final String KEY_7_DAYS = "ranking:sales:7days";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE; // yyyyMMdd

    @Override
    public void increaseSales(Long productId, long quantity) {
        String dailyKey = dailyKey(LocalDate.now());

        redisTemplate.opsForZSet().incrementScore(
                dailyKey,
                productId.toString(),
                quantity
        );

        // TTL: 오늘 + 버퍼 3일 → 총 4일
        // 테스트 환경에서 StackOverflowError 방지를 위해 주석 처리
        // 실제 운영 환경에서는 스케줄러로 오래된 키를 삭제하거나, 별도 방법으로 TTL 관리 필요
        // redisTemplate.expire(dailyKey, Duration.ofDays(4));
    }

    @Override
    public void aggregate(RankingPeriod period) {
        LocalDate today = LocalDate.now();
        List<String> keys = switch (period) {
            case THREE_DAYS -> IntStream.range(0, 3)
                    .mapToObj(i -> dailyKey(today.minusDays(i)))
                    .toList();
            case SEVEN_DAYS -> IntStream.range(0, 7)
                    .mapToObj(i -> dailyKey(today.minusDays(i)))
                    .toList();
            case DAILY -> List.of(dailyKey(today)); // 굳이 집계 필요 없음 (바로 daily 사용)
        };

        // 존재하는 key만 모아 사용 (하루 주문 없어 key가 없을 수도 있으니까)
        List<String> existingKeys = keys.stream()
                .filter(k -> Boolean.TRUE.equals(redisTemplate.hasKey(k)))
                .toList();

        if (existingKeys.isEmpty()) {
            return;
        }

        String destKey = switch (period) {
            case DAILY -> dailyKey(today);
            case THREE_DAYS -> KEY_3_DAYS;
            case SEVEN_DAYS -> KEY_7_DAYS;
        };

        if (existingKeys.size() == 1) {
            // key 하나만 있어도 unionAndStore는 동작하지만, 그냥 copy처럼 쓰기
            redisTemplate.opsForZSet().unionAndStore(
                    existingKeys.get(0),
                    List.of(),
                    destKey
            );
        } else {
            redisTemplate.opsForZSet().unionAndStore(
                    existingKeys.get(0),
                    existingKeys.subList(1, existingKeys.size()),
                    destKey
            );
        }
    }

    @Override
    public List<ProductRankingItem> findTopN(RankingPeriod period, int limit) {
        String key = getKeyForPeriod(period);

        ZSetOperations<String, String> zset = redisTemplate.opsForZSet();

        Set<ZSetOperations.TypedTuple<String>> tuples =
                zset.reverseRangeWithScores(key, 0, limit - 1);

        if (tuples == null || tuples.isEmpty()) {
            return List.of();
        }

        List<ProductRankingItem> result = new ArrayList<>();
        for (ZSetOperations.TypedTuple<String> tuple : tuples) {
            if (tuple.getValue() == null || tuple.getScore() == null) continue;
            Long productId = Long.valueOf(tuple.getValue());
            double score = tuple.getScore();
            result.add(new ProductRankingItem(productId, score));
        }

        return result;
    }

    private String dailyKey(LocalDate date) {
        return DAILY_KEY_PREFIX + date.format(DATE_FORMATTER); // yyyyMMdd
    }

    private String getKeyForPeriod(RankingPeriod period) {
        return switch (period) {
            case DAILY -> dailyKey(LocalDate.now());
            case THREE_DAYS -> KEY_3_DAYS;
            case SEVEN_DAYS -> KEY_7_DAYS;
        };
    }
}

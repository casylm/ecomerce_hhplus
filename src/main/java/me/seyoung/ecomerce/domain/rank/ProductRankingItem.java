package me.seyoung.ecomerce.domain.rank;

public record ProductRankingItem(
        Long productId,
        double score
) {}

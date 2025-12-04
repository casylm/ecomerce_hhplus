package me.seyoung.ecomerce.domain.rank;

public enum RankingPeriod {
    DAILY,
    THREE_DAYS,
    SEVEN_DAYS;

    public static RankingPeriod from(String value) {
        return switch (value.toLowerCase()) {
            case "daily", "day", "1d" -> DAILY;
            case "3days", "3d" -> THREE_DAYS;
            case "7days", "7d", "week" -> SEVEN_DAYS;
            default -> throw new IllegalArgumentException("지원하지 않는 랭킹 기간입니다. period=" + value);
        };
    }
}

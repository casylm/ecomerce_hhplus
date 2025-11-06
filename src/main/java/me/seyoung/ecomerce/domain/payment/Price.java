package me.seyoung.ecomerce.domain.payment;

import lombok.Value;

@Value
public class Price {
    long value;

    public Price add(Price other) {
        return new Price(this.value + other.value);
    }

    public Price subtract(Price other) {
        if (this.value < other.value) {
            throw new IllegalArgumentException("가격은 0보다 작을 수 없습니다.");
        }
        return new Price(this.value - other.value);
    }
}

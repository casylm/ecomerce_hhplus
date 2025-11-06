package me.seyoung.ecomerce.domain.payment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Price 값 객체 테스트")
class PriceTest {

    @Test
    @DisplayName("Price 객체를 생성할 수 있다")
    void Price_생성() {
        // given
        long value = 10000L;

        // when
        Price price = new Price(value);

        // then
        assertThat(price.getValue()).isEqualTo(value);
    }

    @Test
    @DisplayName("두 Price를 더할 수 있다")
    void Price_덧셈() {
        // given
        Price price1 = new Price(10000L);
        Price price2 = new Price(5000L);

        // when
        Price result = price1.add(price2);

        // then
        assertThat(result.getValue()).isEqualTo(15000L);
    }

    @Test
    @DisplayName("Price를 더해도 원본은 변경되지 않는다 (불변성)")
    void Price_덧셈_불변성() {
        // given
        Price price1 = new Price(10000L);
        Price price2 = new Price(5000L);

        // when
        Price result = price1.add(price2);

        // then
        assertThat(price1.getValue()).isEqualTo(10000L); // 원본 유지
        assertThat(price2.getValue()).isEqualTo(5000L);  // 원본 유지
        assertThat(result.getValue()).isEqualTo(15000L); // 새로운 객체
    }

    @Test
    @DisplayName("두 Price를 뺄 수 있다")
    void Price_뺄셈() {
        // given
        Price price1 = new Price(10000L);
        Price price2 = new Price(3000L);

        // when
        Price result = price1.subtract(price2);

        // then
        assertThat(result.getValue()).isEqualTo(7000L);
    }

    @Test
    @DisplayName("Price를 빼도 원본은 변경되지 않는다 (불변성)")
    void Price_뺄셈_불변성() {
        // given
        Price price1 = new Price(10000L);
        Price price2 = new Price(3000L);

        // when
        Price result = price1.subtract(price2);

        // then
        assertThat(price1.getValue()).isEqualTo(10000L); // 원본 유지
        assertThat(price2.getValue()).isEqualTo(3000L);  // 원본 유지
        assertThat(result.getValue()).isEqualTo(7000L);  // 새로운 객체
    }

    @Test
    @DisplayName("뺄셈 결과가 음수가 되면 예외가 발생한다")
    void Price_뺄셈_음수_예외() {
        // given
        Price price1 = new Price(5000L);
        Price price2 = new Price(10000L);

        // when & then
        assertThatThrownBy(() -> price1.subtract(price2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("가격은 0보다 작을 수 없습니다.");
    }

    @Test
    @DisplayName("같은 금액을 빼면 0원이 된다")
    void Price_뺄셈_0원() {
        // given
        Price price1 = new Price(10000L);
        Price price2 = new Price(10000L);

        // when
        Price result = price1.subtract(price2);

        // then
        assertThat(result.getValue()).isEqualTo(0L);
    }

    @Test
    @DisplayName("0원에서 0원을 빼면 0원이다")
    void Price_0원에서_0원_빼기() {
        // given
        Price price1 = new Price(0L);
        Price price2 = new Price(0L);

        // when
        Price result = price1.subtract(price2);

        // then
        assertThat(result.getValue()).isEqualTo(0L);
    }

    @Test
    @DisplayName("0원에 금액을 더할 수 있다")
    void Price_0원에_금액_더하기() {
        // given
        Price price1 = new Price(0L);
        Price price2 = new Price(5000L);

        // when
        Price result = price1.add(price2);

        // then
        assertThat(result.getValue()).isEqualTo(5000L);
    }

    @Test
    @DisplayName("여러 Price를 연속으로 더할 수 있다")
    void Price_연속_덧셈() {
        // given
        Price price1 = new Price(1000L);
        Price price2 = new Price(2000L);
        Price price3 = new Price(3000L);

        // when
        Price result = price1.add(price2).add(price3);

        // then
        assertThat(result.getValue()).isEqualTo(6000L);
    }

    @Test
    @DisplayName("여러 Price를 연속으로 뺄 수 있다")
    void Price_연속_뺄셈() {
        // given
        Price price1 = new Price(10000L);
        Price price2 = new Price(2000L);
        Price price3 = new Price(3000L);

        // when
        Price result = price1.subtract(price2).subtract(price3);

        // then
        assertThat(result.getValue()).isEqualTo(5000L);
    }

    @Test
    @DisplayName("덧셈과 뺄셈을 조합할 수 있다")
    void Price_덧셈_뺄셈_조합() {
        // given
        Price price1 = new Price(10000L);
        Price price2 = new Price(5000L);
        Price price3 = new Price(3000L);

        // when
        Price result = price1.add(price2).subtract(price3);

        // then
        assertThat(result.getValue()).isEqualTo(12000L);
    }

    @Test
    @DisplayName("같은 값을 가진 Price 객체는 동등하다 (값 객체 특성)")
    void Price_동등성() {
        // given
        Price price1 = new Price(10000L);
        Price price2 = new Price(10000L);

        // when & then
        assertThat(price1).isEqualTo(price2);
        assertThat(price1.hashCode()).isEqualTo(price2.hashCode());
    }

    @Test
    @DisplayName("다른 값을 가진 Price 객체는 동등하지 않다")
    void Price_부동등성() {
        // given
        Price price1 = new Price(10000L);
        Price price2 = new Price(20000L);

        // when & then
        assertThat(price1).isNotEqualTo(price2);
    }

    @Test
    @DisplayName("큰 금액의 Price를 생성할 수 있다")
    void Price_큰_금액() {
        // given
        long largeAmount = 1_000_000_000L;

        // when
        Price price = new Price(largeAmount);

        // then
        assertThat(price.getValue()).isEqualTo(largeAmount);
    }

    @Test
    @DisplayName("큰 금액을 더할 수 있다")
    void Price_큰_금액_덧셈() {
        // given
        Price price1 = new Price(500_000_000L);
        Price price2 = new Price(300_000_000L);

        // when
        Price result = price1.add(price2);

        // then
        assertThat(result.getValue()).isEqualTo(800_000_000L);
    }
}

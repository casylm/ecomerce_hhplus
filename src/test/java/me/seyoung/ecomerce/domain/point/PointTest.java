package me.seyoung.ecomerce.domain.point;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class PointTest {
    // 잔액 조회
    @Test
    void 잔액을_조회한다() {
        // given
        Point point = new Point(1L,1000L);

        // when
        long balance = point.getBalance();

        // then
        assertThat(balance).isEqualTo(1000L);
    }

    // 충전
    @Test
    void 금액을_정상적으로_충전한다() {
        // given
        Point point = new Point(2L,0);
        long p = 10000L;

        // when
        point.charge(p);

        // then
        Assertions.assertThat(point.getBalance()).isEqualTo(p);
    }

    // 사용
    @Test
    void 금액을_정상적으로_사용한다() {
        // given
        Point point = new Point(3L,2000L);
        long usePoint = 1000L;

        // when
        point.use(usePoint);

        // then
        Assertions.assertThat(point.getBalance()).isEqualTo(2000L-1000L);
    }

    // 한도초과
    @Test
    void 한도_이상으로_충전하면_예외가_발생한다() {

        // given
        Point point = new Point(4L,900000L);
        long chargePoint = 200000L;

        // when & then
        assertThatThrownBy(() -> point.charge(chargePoint))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("포인트 금액이 충전한도를 초과하였습니다.");
    }

    // 사용금액 초과
    @Test
    void 잔액보다_많은_금액을_사용하면_예외가_발생한다() {

        // given
        Point point = new Point(5L, 5_000L);
        long useAmount = 10_000L;

        // when & then
        assertThatThrownBy(() -> point.use(useAmount))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("포인트가 부족합니다.");
    }
}
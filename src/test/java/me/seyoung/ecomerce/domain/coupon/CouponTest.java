package me.seyoung.ecomerce.domain.coupon;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CouponTest {

    @Test
    void 쿠폰을_생성한다() {
        // given
        String name = "신년 할인 쿠폰";
        int discountAmount = 5000;
        int quantity = 100;

        // when
        Coupon coupon = new Coupon(name, discountAmount, quantity);

        // then
        assertThat(coupon.getName()).isEqualTo(name);
        assertThat(coupon.getDiscountAmount()).isEqualTo(discountAmount);
        assertThat(coupon.getQuantity()).isEqualTo(quantity);
        assertThat(coupon.getIssuedAt()).isNotNull();
    }

    @Test
    void 할인금액이_0이하이면_예외가_발생한다() {
        // given
        String name = "잘못된 쿠폰";
        int discountAmount = 0;
        int quantity = 100;

        // when & then
        assertThatThrownBy(() -> new Coupon(name, discountAmount, quantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("할인 금액은 0보다 커야 합니다.");
    }

    @Test
    void 할인금액이_음수이면_예외가_발생한다() {
        // given
        String name = "잘못된 쿠폰";
        int discountAmount = -1000;
        int quantity = 100;

        // when & then
        assertThatThrownBy(() -> new Coupon(name, discountAmount, quantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("할인 금액은 0보다 커야 합니다.");
    }

    @Test
    void 수량이_음수이면_예외가_발생한다() {
        // given
        String name = "잘못된 쿠폰";
        int discountAmount = 5000;
        int quantity = -1;

        // when & then
        assertThatThrownBy(() -> new Coupon(name, discountAmount, quantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("발급 수량은 0 이상이어야 합니다.");
    }

    @Test
    void 수량이_0인_쿠폰을_생성할_수_있다() {
        // given
        String name = "재고 없는 쿠폰";
        int discountAmount = 5000;
        int quantity = 0;

        // when
        Coupon coupon = new Coupon(name, discountAmount, quantity);

        // then
        assertThat(coupon.getQuantity()).isEqualTo(0);
        assertThat(coupon.hasStock()).isFalse();
    }

    @Test
    void 쿠폰_재고가_있는지_확인한다() {
        // given
        Coupon coupon = new Coupon("할인쿠폰", 5000, 10);

        // when
        boolean hasStock = coupon.hasStock();

        // then
        assertThat(hasStock).isTrue();
    }

    @Test
    void 쿠폰_재고가_0이면_재고가_없다() {
        // given
        Coupon coupon = new Coupon("할인쿠폰", 5000, 0);

        // when
        boolean hasStock = coupon.hasStock();

        // then
        assertThat(hasStock).isFalse();
    }

    @Test
    void 쿠폰_수량을_차감한다() {
        // given
        Coupon coupon = new Coupon("할인쿠폰", 5000, 10);

        // when
        coupon.decreaseQuantity();

        // then
        assertThat(coupon.getQuantity()).isEqualTo(9);
    }

    @Test
    void 쿠폰_수량을_여러번_차감한다() {
        // given
        Coupon coupon = new Coupon("할인쿠폰", 5000, 5);

        // when
        coupon.decreaseQuantity();
        coupon.decreaseQuantity();
        coupon.decreaseQuantity();

        // then
        assertThat(coupon.getQuantity()).isEqualTo(2);
    }

    @Test
    void 수량이_0일때_차감하면_예외가_발생한다() {
        // given
        Coupon coupon = new Coupon("할인쿠폰", 5000, 0);

        // when & then
        assertThatThrownBy(() -> coupon.decreaseQuantity())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("쿠폰 수량이 모두 소진되었습니다.");
    }

    @Test
    void 수량을_모두_차감하면_예외가_발생한다() {
        // given
        Coupon coupon = new Coupon("할인쿠폰", 5000, 2);
        coupon.decreaseQuantity();
        coupon.decreaseQuantity();

        // when & then
        assertThatThrownBy(() -> coupon.decreaseQuantity())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("쿠폰 수량이 모두 소진되었습니다.");
    }
}

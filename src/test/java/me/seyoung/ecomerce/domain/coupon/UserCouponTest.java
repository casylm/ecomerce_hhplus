package me.seyoung.ecomerce.domain.coupon;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class UserCouponTest {

    @Test
    void 사용자_쿠폰을_발급한다() {
        // given
        Long userId = 1L;
        Long couponId = 100L;

        // when
        UserCoupon userCoupon = new UserCoupon(userId, couponId);

        // then
        assertThat(userCoupon.getUserId()).isEqualTo(userId);
        assertThat(userCoupon.getCouponId()).isEqualTo(couponId);
        assertThat(userCoupon.isUsed()).isFalse();
        assertThat(userCoupon.getIssuedAt()).isNotNull();
        assertThat(userCoupon.getUsedAt()).isNull();
    }

    @Test
    void 발급된_쿠폰은_사용_가능하다() {
        // given
        UserCoupon userCoupon = new UserCoupon(1L, 100L);

        // when
        boolean available = userCoupon.isAvailable();

        // then
        assertThat(available).isTrue();
    }

    @Test
    void 쿠폰을_사용한다() {
        // given
        UserCoupon userCoupon = new UserCoupon(1L, 100L);

        // when
        userCoupon.use();

        // then
        assertThat(userCoupon.isUsed()).isTrue();
        assertThat(userCoupon.getUsedAt()).isNotNull();
    }

    @Test
    void 사용된_쿠폰은_사용_불가능하다() {
        // given
        UserCoupon userCoupon = new UserCoupon(1L, 100L);
        userCoupon.use();

        // when
        boolean available = userCoupon.isAvailable();

        // then
        assertThat(available).isFalse();
    }

    @Test
    void 이미_사용된_쿠폰을_다시_사용하면_예외가_발생한다() {
        // given
        UserCoupon userCoupon = new UserCoupon(1L, 100L);
        userCoupon.use();

        // when & then
        assertThatThrownBy(() -> userCoupon.use())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 사용된 쿠폰입니다.");
    }

    @Test
    void 쿠폰_소유자를_확인한다() {
        // given
        Long userId = 1L;
        UserCoupon userCoupon = new UserCoupon(userId, 100L);

        // when
        boolean isOwner = userCoupon.isOwnedBy(userId);

        // then
        assertThat(isOwner).isTrue();
    }

    @Test
    void 다른_사용자는_쿠폰_소유자가_아니다() {
        // given
        UserCoupon userCoupon = new UserCoupon(1L, 100L);

        // when
        boolean isOwner = userCoupon.isOwnedBy(2L);

        // then
        assertThat(isOwner).isFalse();
    }

}

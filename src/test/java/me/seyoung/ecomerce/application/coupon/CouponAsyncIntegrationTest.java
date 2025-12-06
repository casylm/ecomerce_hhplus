package me.seyoung.ecomerce.application.coupon;

import me.seyoung.ecomerce.domain.coupon.Coupon;
import me.seyoung.ecomerce.domain.coupon.CouponRepository;
import me.seyoung.ecomerce.domain.coupon.UserCoupon;
import me.seyoung.ecomerce.domain.coupon.UserCouponRepository;
import me.seyoung.ecomerce.domain.user.User;
import me.seyoung.ecomerce.domain.user.UserRepository;
import me.seyoung.ecomerce.facade.FastIssueCouponFacade;
import me.seyoung.ecomerce.infrastructure.coupon.event.CouponIssueConsumer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class CouponAsyncIntegrationTest {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private FastIssueCouponFacade fastIssueCouponFacade;

    @Autowired
    private CouponIssueConsumer couponIssueConsumer;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void 선착순_쿠폰_비동기_발급_전체_흐름_테스트() throws Exception {
        // given
        Long userId = 1L;

        // 사용자 & 쿠폰 사전 DB 세팅 (테스트 fixture)
        User user = userRepository.save(new User("세영"));
        Coupon coupon = couponRepository.save(new Coupon("1000원 할인", 1000, 10)); // 재고 = 10

        Long couponId = coupon.getId();

        // Redis 초기 재고 세팅
        redisTemplate.opsForValue().set("coupon:stock:" + couponId, "10");

        // when - 선착순 요청 → Redis 등록 + 비동기 큐 적재
        fastIssueCouponFacade.issue(user.getId(), couponId);

        // then - Consumer가 큐에서 꺼내 실제 DB에 저장
        couponIssueConsumer.consume();

        // DB 확인 (실제 발급 되었는지)
        List<UserCoupon> result = userCouponRepository.findByUserId(user.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCouponId()).isEqualTo(couponId);
    }
}

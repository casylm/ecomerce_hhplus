package me.seyoung.ecomerce.application.point;

import me.seyoung.ecomerce.domain.point.Point;
import me.seyoung.ecomerce.domain.point.PointRepository;
import me.seyoung.ecomerce.domain.user.User;
import me.seyoung.ecomerce.domain.user.UserRepository;
import me.seyoung.ecomerce.application.AbstractContainerBaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("포인트 통합 테스트 - Testcontainers MySQL")
class PointIntegrationTest extends AbstractContainerBaseTest {

    @Autowired
    ChargePointUseCase chargePointUseCase;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PointRepository pointRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // 테스트 사용자 생성 및 저장
        testUser = new User("세영");
        testUser = userRepository.save(testUser);
    }

    @Test
    @Transactional
    @DisplayName("실제 DB에 사용자와 포인트가 저장되고 조회된다")
    void 실제_DB_기반_포인트_충전_성공() {
        // when
        PointInfo.ChargePointResponse response = chargePointUseCase.execute(testUser.getId(), 500L);

        // then - 응답 검증
        assertThat(response.getUserId()).isEqualTo(testUser.getId());
        assertThat(response.getCurrentPoint()).isEqualTo(500L);

        // then - Point 엔티티 직접 조회 및 DB 값 확인
        Point savedPoint = pointRepository.findByUserId(testUser.getId()).orElseThrow();
        assertThat(savedPoint.getBalance()).isEqualTo(500L);
        assertThat(savedPoint.getUserId()).isEqualTo(testUser.getId());
    }

    @Test
    @Transactional
    @DisplayName("포인트를 여러 번 충전하면 잔액이 누적된다")
    void 포인트_여러번_충전_성공() {
        // given
        chargePointUseCase.execute(testUser.getId(), 1000L);

        // when
        chargePointUseCase.execute(testUser.getId(), 500L);
        PointInfo.ChargePointResponse response = chargePointUseCase.execute(testUser.getId(), 300L);

        // then - 응답 검증
        assertThat(response.getCurrentPoint()).isEqualTo(1800L);

        // then - DB 값 직접 확인
        Point savedPoint = pointRepository.findByUserId(testUser.getId()).orElseThrow();
        assertThat(savedPoint.getBalance()).isEqualTo(1800L);
    }
}

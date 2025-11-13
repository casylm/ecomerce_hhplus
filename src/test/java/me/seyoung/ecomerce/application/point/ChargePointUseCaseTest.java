package me.seyoung.ecomerce.application.point;

import me.seyoung.ecomerce.domain.point.Point;
import me.seyoung.ecomerce.domain.point.PointRepository;
import me.seyoung.ecomerce.domain.user.User;
import me.seyoung.ecomerce.domain.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChargePointUseCaseTest {

    @Mock
    @Qualifier("pointRepositoryImpl") // JPA 구현체 Bean 이름
    PointRepository pointRepository;

    @Mock
    @Qualifier("userRepositoryImpl") // JPA 구현체 Bean 이름
    UserRepository userRepository;


    @InjectMocks
    ChargePointUseCase chargePointUseCase;

    @Test
    void 포인트_충전_성공() {
        // given
        User user = new User(1L, "테스트유저");
        Point point = new Point(1L, 1000L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(pointRepository.findByUserId(1L)).thenReturn(Optional.of(point));

        // when
        chargePointUseCase.execute(1L, 500L);

        // then
        assertThat(point.getBalance()).isEqualTo(1500L);
        verify(userRepository).findById(1L);
        verify(pointRepository).findByUserId(1L);
        verify(pointRepository).save(any(Point.class));
    }
}

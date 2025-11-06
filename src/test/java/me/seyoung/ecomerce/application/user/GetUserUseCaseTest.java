package me.seyoung.ecomerce.application.user;

import me.seyoung.ecomerce.domain.user.User;
import me.seyoung.ecomerce.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetUserUseCase 단위 테스트")
class GetUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GetUserUseCase getUserUseCase;

    @Test
    @DisplayName("존재하는 사용자를 조회할 수 있다")
    void 존재하는_사용자_조회() {
        // given
        Long userId = 1L;
        User user = new User(userId, "홍길동");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        UserInfo result = getUserUseCase.execute(userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getName()).isEqualTo("홍길동");
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("존재하지 않는 사용자를 조회하면 예외가 발생한다")
    void 존재하지_않는_사용자_조회_시_예외() {
        // given
        Long userId = 999L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> getUserUseCase.execute(userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 사용자입니다. userId=" + userId);
    }

    @Test
    @DisplayName("사용자 존재 여부를 확인할 수 있다")
    void 사용자_존재_여부_확인() {
        // given
        Long existingUserId = 1L;
        Long nonExistingUserId = 999L;

        given(userRepository.existsById(existingUserId)).willReturn(true);
        given(userRepository.existsById(nonExistingUserId)).willReturn(false);

        // when
        boolean exists = getUserUseCase.exists(existingUserId);
        boolean notExists = getUserUseCase.exists(nonExistingUserId);

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
        verify(userRepository).existsById(existingUserId);
        verify(userRepository).existsById(nonExistingUserId);
    }
}

package me.seyoung.ecomerce.application.user;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.domain.user.User;
import me.seyoung.ecomerce.domain.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetUserUseCase {

    private final UserRepository userRepository;

    public UserInfo execute(Long userId) {
        // 사용자 존재 여부 확인 및 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. userId=" + userId));

        // 응답 반환
        return UserInfo.from(user);
    }

    public boolean exists(Long userId) {
        return userRepository.existsById(userId);
    }
}

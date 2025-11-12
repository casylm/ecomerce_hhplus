package me.seyoung.ecomerce.presentation.user;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.application.user.GetUserUseCase;
import me.seyoung.ecomerce.application.user.UserInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final GetUserUseCase getUserUseCase;

    @GetMapping("/{userId}")
    public ResponseEntity<UserInfo> getUser(@PathVariable Long userId) {
        UserInfo response = getUserUseCase.execute(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/exists")
    public ResponseEntity<UserExistsResponse> checkUserExists(@PathVariable Long userId) {
        boolean exists = getUserUseCase.exists(userId);
        return ResponseEntity.ok(new UserExistsResponse(userId, exists));
    }

    public record UserExistsResponse(Long userId, boolean exists) {}
}

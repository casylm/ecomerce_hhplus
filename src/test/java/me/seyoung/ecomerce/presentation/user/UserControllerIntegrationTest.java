package me.seyoung.ecomerce.presentation.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.seyoung.ecomerce.domain.user.User;
import me.seyoung.ecomerce.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("UserController 통합 테스트")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private Long testUserId;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 생성
        User user = new User(1L, "홍길동");
        User savedUser = userRepository.save(user);
        testUserId = savedUser.getId();
    }

    @Test
    @DisplayName("사용자 조회 성공")
    void 사용자_조회_성공() throws Exception {
        // when & then
        mockMvc.perform(get("/api/users/{userId}", testUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(testUserId.intValue())))
                .andExpect(jsonPath("$.name", is("홍길동")))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("존재하지 않는 사용자 조회 시 예외 발생")
    void 존재하지_않는_사용자_조회_시_예외() throws Exception {
        // given
        Long nonExistentUserId = 999L;

        // when & then
        mockMvc.perform(get("/api/users/{userId}", nonExistentUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("사용자 존재 여부 확인 - 존재하는 경우")
    void 사용자_존재_여부_확인_존재() throws Exception {
        // when & then
        mockMvc.perform(get("/api/users/{userId}/exists", testUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(testUserId.intValue())))
                .andExpect(jsonPath("$.exists", is(true)));
    }

    @Test
    @DisplayName("사용자 존재 여부 확인 - 존재하지 않는 경우")
    void 사용자_존재_여부_확인_미존재() throws Exception {
        // given
        Long nonExistentUserId = 999L;

        // when & then
        mockMvc.perform(get("/api/users/{userId}/exists", nonExistentUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(nonExistentUserId.intValue())))
                .andExpect(jsonPath("$.exists", is(false)));
    }

    @Test
    @DisplayName("여러 사용자를 저장하고 각각 조회할 수 있다")
    void 여러_사용자_저장_및_조회() throws Exception {
        // given
        User user2 = new User(2L, "김철수");
        User user3 = new User(3L, "이영희");
        User savedUser2 = userRepository.save(user2);
        User savedUser3 = userRepository.save(user3);

        // when & then
        mockMvc.perform(get("/api/users/{userId}", savedUser2.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("김철수")));

        mockMvc.perform(get("/api/users/{userId}", savedUser3.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("이영희")));
    }
}

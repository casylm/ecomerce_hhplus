package me.seyoung.ecomerce.infrastructure.user;

import me.seyoung.ecomerce.domain.user.User;
import me.seyoung.ecomerce.domain.user.UserRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryUserRepository implements UserRepository {

    // 사용자를 저장할 메모리 Map
    private final Map<Long, User> store = new ConcurrentHashMap<>();

    @Override
    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(store.get(userId));
    }

    @Override
    public Optional<User> findByName(String name) {
        return store.values().stream()
                .filter(user -> user.getName().equals(name))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public User save(User user) {
        store.put(user.getId(), user);
        return user;
    }

    @Override
    public boolean existsById(Long userId) {
        return store.containsKey(userId);
    }

    // 기존 메서드 호환성 유지 (deprecated)
    @Deprecated
    public Optional<User> findByUserId(Long userId) {
        return findById(userId);
    }
}

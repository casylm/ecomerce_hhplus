package me.seyoung.ecomerce.infrastructure.user;

import me.seyoung.ecomerce.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long userId);
    Optional<User> findByName(String name);
    List<User> findAll();
    User save(User user);
    boolean existsById(Long userId);
}

package me.seyoung.ecomerce.domain.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(Long userId);
    Optional<User> findByName(String name);
    List<User> findAll();
    User save(User user);
    boolean existsById(Long userId);
}

package me.seyoung.ecomerce.domain.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    /**
     * 사용자 ID로 조회
     * @param userId 사용자 ID
     * @return 사용자 Optional
     */
    Optional<User> findById(Long userId);

    /**
     * 사용자 이름으로 조회
     * @param name 사용자 이름
     * @return 사용자 Optional
     */
    Optional<User> findByName(String name);

    /**
     * 모든 사용자 조회
     * @return 사용자 목록
     */
    List<User> findAll();

    /**
     * 사용자 저장
     * @param user 저장할 사용자
     * @return 저장된 사용자
     */
    User save(User user);

    /**
     * 사용자 존재 여부 확인
     * @param userId 사용자 ID
     * @return 존재 여부
     */
    boolean existsById(Long userId);
}

package com.dockersim.repository;

import com.dockersim.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 이메일로 사용자 조회
     */
    Optional<User> findByEmail(String email);

    Optional<User> findByPublicId(String publicId);

    Optional<User> findByGithubId(String githubId);

    /**
     * 이메일 중복 확인
     */
    boolean existsByEmail(String email);
}
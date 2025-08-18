package com.dockersim.repository;

import com.dockersim.domain.Simulation;
import com.dockersim.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SimulationRepository extends JpaRepository<Simulation, Long> {

    /**
     * 소유자로 시뮬레이션 목록 조회
     */
    List<Simulation> findByUser(User user);

    /**
     * 소유자와 ID로 시뮬레이션 조회 (권한 확인용)
     */
    Optional<Simulation> findByIdAndUser(Long id, User user);

    /**
     * 제목 중복 확인 (같은 소유자 내에서)
     */
    boolean existsByTitleAndUser(String title, User user);

    /**
     * 소유자 ID로 시뮬레이션 목록 조회
     */
    List<Simulation> findByUserId(Long userId);
}
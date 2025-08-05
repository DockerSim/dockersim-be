package com.dockersim.repository;

import com.dockersim.domain.Container;
import com.dockersim.domain.ContainerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 컨테이너 Repository
 */
@Repository
public interface ContainerRepository extends JpaRepository<Container, Long> {

    /**
     * 시뮬레이션에 속한 모든 컨테이너 조회
     */
    List<Container> findBySimulationId(Long simulationId);

    /**
     * 시뮬레이션에 속한 특정 상태의 컨테이너들 조회
     */
    List<Container> findBySimulationIdAndStatus(Long simulationId, ContainerStatus status);

    /**
     * 컨테이너 ID로 조회
     */
    Optional<Container> findByContainerId(String containerId);

    /**
     * 시뮬레이션에 속한 특정 이름의 컨테이너 조회
     */
    Optional<Container> findBySimulationIdAndName(Long simulationId, String name);

    /**
     * 시뮬레이션과 컨테이너 ID로 조회
     */
    Optional<Container> findBySimulationIdAndContainerId(Long simulationId, String containerId);

    /**
     * 시뮬레이션의 실행 중인 컨테이너 수 계산
     */
    @Query("SELECT COUNT(c) FROM Container c WHERE c.simulation.id = :simulationId AND c.status = 'RUNNING'")
    Long countRunningContainersBySimulationId(@Param("simulationId") Long simulationId);

    /**
     * 시뮬레이션의 전체 컨테이너 수 계산
     */
    Long countBySimulationId(Long simulationId);

    /**
     * 시뮬레이션에서 이름 또는 ID로 컨테이너 검색
     */
    @Query("SELECT c FROM Container c WHERE c.simulation.id = :simulationId " +
            "AND (c.name LIKE %:searchTerm% OR c.containerId LIKE %:searchTerm%)")
    List<Container> findBySimulationIdAndNameOrIdContaining(
            @Param("simulationId") Long simulationId,
            @Param("searchTerm") String searchTerm);

    /**
     * 특정 이미지를 사용하는 컨테이너들 조회
     */
    List<Container> findBySimulationIdAndImage(Long simulationId, String image);
}
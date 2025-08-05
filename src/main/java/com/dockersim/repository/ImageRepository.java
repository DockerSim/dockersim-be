package com.dockersim.repository;

import com.dockersim.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 이미지 Repository
 */
@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    /**
     * 시뮬레이션에 속한 모든 이미지 조회
     */
    List<Image> findBySimulationId(Long simulationId);

    /**
     * 이미지 ID로 조회
     */
    Optional<Image> findByImageId(String imageId);

    /**
     * 시뮬레이션과 이미지 ID로 조회
     */
    Optional<Image> findBySimulationIdAndImageId(Long simulationId, String imageId);

    /**
     * 시뮬레이션에서 repository와 tag로 이미지 조회
     */
    Optional<Image> findBySimulationIdAndRepositoryAndTag(
            Long simulationId, String repository, String tag);

    /**
     * 이미지명(repository:tag 형태)으로 이미지 조회
     * nginx:latest, nginx 등의 형태로 검색
     */
    @Query("SELECT i FROM Image i WHERE i.simulation.id = :simulationId " +
            "AND ((i.repository = :imageName) OR (CONCAT(i.repository, ':', i.tag) = :imageName))")
    Optional<Image> findBySimulationIdAndImageName(
            @Param("simulationId") Long simulationId,
            @Param("imageName") String imageName);

    /**
     * repository 이름으로 이미지 검색 (부분 일치)
     */
    @Query("SELECT i FROM Image i WHERE i.simulation.id = :simulationId " +
            "AND i.repository LIKE %:repository%")
    List<Image> findBySimulationIdAndRepositoryContaining(
            @Param("simulationId") Long simulationId,
            @Param("repository") String repository);

    /**
     * 정확한 repository로 이미지들 조회
     */
    List<Image> findBySimulationIdAndRepository(Long simulationId, String repository);

    /**
     * 특정 이미지명이 존재하는지 확인
     */
    @Query("SELECT COUNT(i) > 0 FROM Image i WHERE i.simulation.id = :simulationId " +
            "AND ((i.repository = :imageName) OR (CONCAT(i.repository, ':', i.tag) = :imageName))")
    boolean existsBySimulationIdAndImageName(@Param("simulationId") Long simulationId,
            @Param("imageName") String imageName);

    /**
     * 시뮬레이션의 전체 이미지 수 계산
     */
    Long countBySimulationId(Long simulationId);
}
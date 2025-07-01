// 이 인터페이스는 Container 엔티티에 대한 데이터 액세스를 담당합니다.
// 주요 메서드:
// - findByContainerId : 컨테이너 ID로 검색
// - findByName : 컨테이너 이름으로 검색
// - findByStatus : 상태별 컨테이너 목록 조회
// - findByImageName : 이미지 이름별 컨테이너 목록 조회

package com.dockersim.repository;

import com.dockersim.domain.Container;
import com.dockersim.domain.ContainerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContainerRepository extends JpaRepository<Container, Long> {

    Optional<Container> findByContainerId(String containerId);

    Optional<Container> findByName(String name);

    List<Container> findByStatus(ContainerStatus status);

    List<Container> findByImageName(String imageName);

    boolean existsByName(String name);

    boolean existsByContainerId(String containerId);
}
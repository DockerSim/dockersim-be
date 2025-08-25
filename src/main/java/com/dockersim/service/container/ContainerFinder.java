package com.dockersim.service.container;

import com.dockersim.domain.ContainerStatus;
import com.dockersim.domain.DockerContainer;
import com.dockersim.domain.Simulation;
import java.util.List;
import java.util.Optional;

public interface ContainerFinder {

    /**
     * 특정 이미지 ID를 기반으로 하며 특정 상태에 있는 컨테이너가 존재하는지 확인합니다.
     *
     * @param imageId 확인할 기반 이미지의 ID
     * @param status  확인할 컨테이너 상태
     * @return 존재하면 true, 그렇지 않으면 false
     */
    boolean existsByImageIdAndStatus(String imageId, ContainerStatus status);

    /**
     * 현재 시뮬레이션의 모든 컨테이너를 조회합니다.
     *
     * @param simulation 현재 시뮬레이션
     * @return 컨테이너 목록
     */
    List<DockerContainer> findAllBySimulation(Simulation simulation);

    /**
     * 컨테이너 이름으로 컨테이너를 조회합니다.
     *
     * @param name       조회할 컨테이너 이름
     * @param simulation 현재 시뮬레이션
     * @return Optional<DockerContainer>
     */
    Optional<DockerContainer> findByName(String name, Simulation simulation);

    /**
     * 컨테이너 ID로 컨테이너를 조회합니다.
     *
     * @param containerId 조회할 컨테이너 ID
     * @param simulation  현재 시뮬레이션
     * @return Optional<DockerContainer>
     */
    Optional<DockerContainer> findByContainerId(String containerId, Simulation simulation);


    /**
     * 컨테이너의 이름 또는 ID와 컨테이너 실행 상태, 시뮬레이션 ID로 특정 상태의 컨테이너를 조회합니다.
     *
     * @param nameOrId     컨테이너의 이름 또는 ID
     * @param status       컨테이너의 상태
     * @param simulationId 컨테이너가 속한 시뮬레이션의 UUID(String)
     * @return 조회환 컨테이너 반환
     */
    DockerContainer findByNameOrIdAndStatusAndSimulationId(String nameOrId, ContainerStatus status,
        String simulationId);
}

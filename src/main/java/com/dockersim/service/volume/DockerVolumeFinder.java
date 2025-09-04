package com.dockersim.service.volume;

import com.dockersim.domain.DockerVolume;
import java.util.List;

public interface DockerVolumeFinder {

    /**
     * 불륨명으로 불륨 존재 여부를 확인합니다.
     *
     * @param name         조회할 볼륨명
     * @param simulationId 볼륨이 속한 시뮬레이션의 ID
     */
    boolean existsByNameAndSimulationId(String name, Long simulationId);

    /**
     * 볼륨명으로 볼륨을 조회합니다.
     *
     * @param name         조회할 볼륨명
     * @param simulationId 볼륨이 속한 시뮬레이션의 ID
     */
    DockerVolume findByNameAndSimulationId(String name, Long simulationId);

    /**
     * 시뮬레이션에 속한 익명/명명 볼륨을 조회합니다.
     *
     * @param simulationId 볼륨들을 가져올 시뮬레이션의 ID
     */
    List<DockerVolume> findBySimulationId(Long simulationId);

    /**
     * 시뮬레이션 내에서 사용하지 않는 '익명' 볼륨을 모두 찾습니다. (containerVolumes 컬렉션이 비어있고, anonymous 플래그가 true인 경우)
     *
     * @param simulationId 볼륨들을 가져올 시뮬레이션의 ID
     */
    List<DockerVolume> findUnusedAnonymousVolumes(Long simulationId);

    /**
     * 시뮬레이션 내에서 사용하지 않는 '모든' 볼륨(익명 + 명명)을 찾습니다. (containerVolumes 컬렉션이 비어있는 경우)
     *
     * @param simulationId 볼륨들을 가져올 시뮬레이션의 ID
     */
    List<DockerVolume> findAllUnusedVolumes(Long simulationId);

    /**
     * 볼륨명으로 시뮬레이션 내에서 사용하지 않는 볼륨을 찾습니다.
     *
     * @param name         조회할 볼륨명
     * @param simulationId 볼륨이 속한 시뮬레이션의 ID
     */
    DockerVolume findUnusedVolumeByNameAndSimulationId(String name, Long simulationId);

}

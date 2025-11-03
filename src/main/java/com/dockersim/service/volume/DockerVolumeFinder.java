package com.dockersim.service.volume;

import java.util.List;

import com.dockersim.domain.DockerVolume;
import com.dockersim.domain.Simulation;

public interface DockerVolumeFinder {

	/**
	 * 불륨명으로 불륨 존재 여부를 확인합니다.
	 *
	 * @param simulation 볼륨이 속한 Simulation
	 * @param name       조회할 볼륨명
	 */
	boolean existsBySimulationAndName(Simulation simulation, String name);

	/**
	 * 볼륨명으로 조회합니다.
	 *
	 * @param simulation 볼륨이 속한 Simulation
	 * @param name       조회할 볼륨명
	 */
	DockerVolume findBySimulationAndName(Simulation simulation, String name);

	/**
	 * 시뮬레이션에 속한 익명/명명 볼륨을 조회합니다.
	 *
	 * @param simulation 볼륨이 속한 Simulation
	 */
	List<DockerVolume> findBySimulation(Simulation simulation);

	/**
	 * 시뮬레이션 내 사용하지 않는 익명/명명 볼륨을 조회합니다.
	 *
	 * 사용하지 않는 볼륨의 기준은 containerVolumes 컬렉션이 비어있는 경우를 의미합니다.
	 *
	 * 기본적으로 익명 볼륨을 조회합니다.
	 * all 플래그가 활성화 되면 익명/명명 볼륨 모두 조회합니다.
	 *
	 * @param simulation 볼륨이 속한 Simulation
	 * @param all  기본 동작 변경: 익명/명명 볼륨 모두 조회
	 */
	List<DockerVolume> findUnusedVolumes(Simulation simulation, boolean all);

	/**
	 * 볼륨명으로 시뮬레이션 내에서 사용하지 않는 볼륨을 삭제합니다.
	 *
	 * @param simulation 볼륨이 속한 Simulation
	 * @param name       삭제할 볼륨명
	 */
	DockerVolume findUnusedVolumeBySimulationAndName(Simulation simulation, String name);

}

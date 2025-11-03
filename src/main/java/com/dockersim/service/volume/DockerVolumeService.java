package com.dockersim.service.volume;

import java.util.List;

import com.dockersim.config.SimulationUserPrincipal;
import com.dockersim.dto.response.DockerVolumeResponse;

public interface DockerVolumeService {

	/**
	 * 도커 불륨(Volume)을 생성합니다.
	 *
	 * 컨테이너 실행(run -v)은 볼륨 엔티티를 생성하지 않습니다.
	 * 볼륨 마운트 지정은 드라이버를 사용하지만, 본 서비스에서는 드라이버에 대한 학습 지원을 하지 않습니다.
	 * 따라서 기본 마운트 경로만 사용합니다.
	 *
	 * name 생략 된 익명 불륨(anonymous)은 임의의 Hex ID를 이름으로 가집니다.
	 *
	 * 이미 존재하는 이름의 불륨 생성 시 실패합니다.
	 *
	 * @param principal 인증 정보
	 * @param name      불륨명
	 * @param anonymous 익명 볼륨 여부, prune -a의 빠른 식별을 위한 옵션입니다.
	 * @return 생성된 불륨 응답 정보
	 */
	DockerVolumeResponse create(SimulationUserPrincipal principal, String name, boolean anonymous);

	/**
	 * 도커 볼륨의 상세 정보를 출력합니다.
	 *
	 * @param principal 인증 정보
	 * @param name      조회할 불륨명
	 * @return 조회된 불륨 상세 정보
	 */
	List<String> inspect(SimulationUserPrincipal principal, String name);

	/**
	 * 도커 볼륨 목록을 출력합니다.
	 *
	 * @param principal 인증 정보
	 * @param quiet     불륨명만 출력 여부
	 * @return 조회된 불륨명 목록
	 */
	List<String> ls(SimulationUserPrincipal principal, boolean quiet);

	/**
	 * Remove all unused local volumes. Unused local volumes are those which are not referenced by
	 * any containers. By default, it only removes anonymous volumes.
	 *
	 * @param principal 인증 정보
	 * @param all       익명 볼륨 뿐 아니라, 참조되지 않는 불륨의 삭제 여부
	 * @return 삭제된 불륨명 목록
	 */
	List<DockerVolumeResponse> prune(SimulationUserPrincipal principal, boolean all);

	/**
	 * Remove one or more volumes. You can't remove a volume that's in use by a container.
	 *
	 * @param principal 인증 정보
	 * @param name      삭제할 불륨명
	 * @return 삭제된 불륨명
	 */
	DockerVolumeResponse rm(SimulationUserPrincipal principal, String name);
}

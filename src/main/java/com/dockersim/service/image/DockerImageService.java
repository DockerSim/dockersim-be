package com.dockersim.service.image;

import java.util.List;

import com.dockersim.config.SimulationUserPrincipal;
import com.dockersim.dto.response.DockerImageResponse;

public interface DockerImageService {

	/**
	 * Dockerfile을 기반으로 새로운 Image를 생성합니다.
	 *
	 * @param principal      인증 정보
	 * @param dockerFilePath 도커 파일 경로
	 * @param tag            생성되는 Image의 repo[:tag] 지정
	 * @return 생성한 Image와 콘솔 결과를 밥환합니다.
	 */
	DockerImageResponse build(SimulationUserPrincipal principal, String dockerFilePath,
		String tag);

	/**
	 * Image의 Layer를 전부 출력합니다.
	 *
	 * @param principal   인증 정보
	 * @param nameOrHexId Layer를 조회할 Image 이름 또는 Hex ID
	 * @return 콘솔 결과를 반환합니다.
	 */
	List<String> history(SimulationUserPrincipal principal, String nameOrHexId);

	/**
	 * Image의 상세 정보를 전부 반환합니다.
	 *
	 * @param principal   인증 정보
	 * @param nameOrHexId 상세 정보를 조회할 Image 이름 또는 Hex ID
	 * @return 콘솔 결과를 반환합니다.
	 */
	List<String> inspect(SimulationUserPrincipal principal, String nameOrHexId);

	/**
	 * Local에 저장된 Image를 조회합니다.
	 *
	 * @param principal 인증 정보
	 * @param all       댕글링 이미지도 조회합니다.
	 * @param quiet     Image Hex ID만 출력합니다.
	 * @return 콘솔 결과를 반환합니다.
	 */
	List<String> ls(SimulationUserPrincipal principal, boolean all, boolean quiet);

	/**
	 * Local의 모든 댕글링 이미지를 삭제합니다.
	 *
	 * @param principal 인증 정보
	 * @param all       참조되지 않는 이미지도 삭제합니다.
	 * @return 삭제할 Image와 콘솔 결과를 반환합니다.
	 */
	List<DockerImageResponse> prune(SimulationUserPrincipal principal, boolean all);

	/**
	 * Docker Hub 또는 원격 이미지 저장소에서 Local로 Image를 다운로드 합니다.
	 *
	 * @param principal 인증 정보
	 * @param name      다운로드 받을 Image 이름
	 * @param allTags   동일한 repo의 모든 Image를 다운로드합니다.
	 * @return 다운로드 받은(새로 생성된) Image와 콘솔 결과를 반환합니다.
	 */
	List<DockerImageResponse> pull(SimulationUserPrincipal principal, String name, boolean allTags);

	/**
	 * Local의 Image를 사용자 저장소로 업로드합니다.
	 *
	 * @param principal 인증 정보
	 * @param name      업로드할 도커명
	 * @param allTags   동일한 repo의 모든 Image를 업로드합니다..
	 * @return 사용자 저장소에 업로드된(새로 생성된) Image와 콘솔 결과를 반환합니다.
	 */
	List<DockerImageResponse> push(SimulationUserPrincipal principal, String name, boolean allTags);

	/**
	 * Local의 Image를 삭제합니다.
	 *
	 * @param principal   인증 정보
	 * @param nameOrHexId 삭제할 Image 이름 또는 Hex ID
	 * @param force       해당 Image가 다른 Container의 Base Image이여도 삭제합니다.
	 * @return 삭제한 Image와 콘솔 결과를 반환합니다.
	 */
	DockerImageResponse rm(SimulationUserPrincipal principal, String nameOrHexId, boolean force);
}
package com.dockersim.service.image;

import java.util.List;
import java.util.Map;

import com.dockersim.domain.DockerImage;
import com.dockersim.domain.ImageLocation;
import com.dockersim.domain.Simulation;

public interface DockerImageFinder {

	/**
	 * location에 조건에 맞는 이미지를 찾습니다.
	 *
	 * @param simulation Image가 속한 simulation
	 * @param namespace 이미지 네임스페이스
	 * @param name      이미지 레포명
	 * @param tag       이미지 태그
	 * @param location  이미지가 저장된 위치
	 * @return 결과가 없을 경우 예외가 아닌, null 을 반환합니다.
	 */
	DockerImage findSameImage(Simulation simulation, String namespace, String name, String tag, ImageLocation location);

	/**
	 * location에서 Hex ID로 Image를 찾습니다.
	 *
	 * @param simulation Image가 속한 simulation
	 * @param hexId      Image의 Hex ID
	 * @param location   Image가 저장된 위치
	 */
	DockerImage findSameImage(Simulation simulation, String hexId, ImageLocation location);

	/**
	 * Image 이름 형식이 명확하지 않을 떄 통합적으로 탐색하는 메서드 입니다.
	 * 일반 이름 형식 또는 Hex ID에 대해 findSameImage를 호출해 탐색합니다.
	 *
	 * @param simulation Image가 속한 simulation
	 * @param imageInfo  Image Name의 파싱 데이터
	 * @param location   Image가 저장된 위치
	 * @param hexId      Image의 Hex ID
	 */
	DockerImage findImageByNameOrId(Simulation simulation, Map<String, String> imageInfo, ImageLocation location,
		String hexId);

	/**
	 * Loacl에서 Image를 조회합니다.
	 *
	 * @param simulation Image가 속한 simulation
	 * */
	List<DockerImage> findBySimulationInLocal(Simulation simulation, boolean all);

	// ----

	/**
	 * 이미지 이름 또는 ID로 이미지를 검색합니다. 이름, 태그, 전체 ID, 부분 ID를 포함하여 검색하며, 모호한 경우 여러 개를 반환할 수 있습니다.
	 *
	 * @param imageNameOrId 검색할 이미지 이름 또는 ID
	 * @param simulation    검색할 시뮬레이션 컨텍스트
	 * @return 검색된 이미지 목록
	 */
	List<DockerImage> findImages(String imageNameOrId, Simulation simulation);

	/**
	 * 시뮬레이션에 속한 이미지 중 location 이 일치하는 이미지를 반환합니다.
	 *
	 * @param simulation 이미지가 속한 시뮬레이션 객체
	 * @param location   이미지가 저장된 위치
	 * @return 검색된 이미지 목록
	 */
	List<DockerImage> getImages(Simulation simulation, ImageLocation location);

}

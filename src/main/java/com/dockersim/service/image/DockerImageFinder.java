package com.dockersim.service.image;

import java.util.List;
import java.util.Map;

import com.dockersim.domain.DockerImage;
import com.dockersim.domain.ImageLocation;
import com.dockersim.domain.Simulation;
import com.dockersim.dto.util.ImageMeta;

public interface DockerImageFinder {

	/**
	 * namespace, name, tag, location이 동일한 Image를 조회합니다.
	 *
	 * @param simulation Image가 속한 simulation
	 * @param meta       Image의 메타 정보
	 * @param location   Image가 저장된 위치(LOCAL, HUB)
	 */
	DockerImage findImageOrNull(
		Simulation simulation,
		ImageMeta meta,
		ImageLocation location
	);

	/**
	 * namespace, name, location이 동일한 Image들을 조회합니다.
	 *
	 * @param simulation Image가 속한 simulation
	 * @param meta       Image의 메타 정보
	 * @param location   Image가 저장된 위치(LOCAL, HUB)
	 */
	List<DockerImage> findImages(
		Simulation simulation,
		ImageMeta meta,
		ImageLocation location
	);

	/**
	 * Hub에서 PUll할 Image를 조회합니다.
	 *
	 * @param simulation Image가 속한 simulation
	 * @param meta       Image의 메타 정보
	 * @param allTags    tag에 관계없이, namespace/name이 동일한 Image를 전부 조회합니다.
	 */
	List<DockerImage> findPullImageByInfo(Simulation simulation, ImageMeta meta, boolean allTags);

	/**
	 * Local에서 Push할 Image들을 조회합니다.
	 *
	 * @param simulation Image가 속한 simulation
	 * @param meta       Image의 메타 정보
	 * @param allTags    tag에 관계없이, namespace/name이 동일한 Image를 전부 조회합니다.
	 */
	List<DockerImage> findPushImageInLocal(
		Simulation simulation,
		ImageMeta meta,
		boolean allTags
	);

	/**
	 * Hub에서 Push할 Image와 동일한 name:tag를 가진 Image를 조회합니다.
	 *
	 * @param simulation  Image가 속한 simulation
	 * @param localImages Push할 Image 목록
	 * @param meta        Image의 메타 정보
	 * @param allTags     tag에 관계없이, namespace/name이 동일한 Image를 전부 조회합니다.
	 */
	List<DockerImage> findOldPushImageInHub(
		Simulation simulation,
		List<DockerImage> localImages,
		ImageMeta meta,
		boolean allTags
	);

	// -----------------------------------------------------------------
	DockerImage findBySimulationAndNamespaceAndNameAndLocationOrNull(
		Simulation simulation,
		String namespace,
		String name,
		ImageLocation location
	);

	DockerImage findImageOrNull(Simulation simulation, Map<String, String> imageInfo,
		ImageLocation location);

	DockerImage findImageInLocalOrNull(Simulation simulation, Map<String, String> imageInfo);

	DockerImage findImageInHubOrNull(Simulation simulation, Map<String, String> imageInfo);

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
	DockerImage findImageByNameOrId(Simulation simulation, ImageMeta imageInfo, ImageLocation location,
		String hexId);

	/**
	 * Loacl에서 Image를 조회합니다.
	 *
	 * @param simulation Image가 속한 simulation
	 * @param all
	 * */
	List<DockerImage> findBySimulationInLocal(Simulation simulation, boolean all);

	/**
	 * Local에서 댕글링 이미지를 조회합니다.
	 *
	 * @param simulation Image가 속한 simulation
	 */
	List<DockerImage> findDanglingImageBySimulationInLocal(Simulation simulation);

	/**
	 * Local에서 참조되지 않는 Image를 조회합니다.
	 * @param simulation Image가 속한 simulation
	 */
	List<DockerImage> findUnreferencedImageBySimulationInLocal(Simulation simulation);

}

package com.dockersim.service.image;

import com.dockersim.domain.DockerImage;
import com.dockersim.domain.ImageLocation;
import com.dockersim.domain.Simulation;
import com.dockersim.dto.util.ImageMeta;

import java.util.List;

public interface DockerImageFinder {
	/*
	Common
	 */

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
     * Image가 Hex ID인지, Name인지 모를 때 location에서 조회합니다.
     * 기본적으로 Name으로 조회,
     * 조회되지 않았을 경우 Hex ID로 조회합니다.
     *
     * @param simulation Image가 속한 simulation
     * @param meta       Image의 메타 정보
     * @param location   어디서 Image를 찾을지 지정
     */
    DockerImage findImageByNameBeforeShortHexId(Simulation simulation, ImageMeta meta, ImageLocation location);

    /**
     * namespace, name, location이 동일한 Image를 조회합니다.
     *
     * @param simulation Image가 속한 simulation
     * @param meta       Image의 메타 정보
     * @param location   Image가 저장된 위치(LOCAL, HUB)
     */
    DockerImage findImage(
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


	/*
	image build
 	 */

    /**
     * Local에 name과 tag가 동일한 Image가 있는지 조회합니다.
     *
     * @param simulation Image가 속한 simulation
     * @param meta       Image의 메타 정보
     */
    DockerImage findImageInLocalOrNull(Simulation simulation, ImageMeta meta);
	/*
	image ls
	 */

    /**
     * Loacl에 있는 모든 Image를 조회합니다.
     *
     * @param simulation Image가 속한 simulation
     * @param all
     *
     */
    List<DockerImage> findBySimulationInLocal(Simulation simulation, boolean all);

	/*
	image pull
	 */

    /**
     * Hub에서 PUll할 Image를 조회합니다.
     *
     * @param simulation Image가 속한 simulation
     * @param meta       Image의 메타 정보
     * @param allTags    tag에 관계없이, namespace/name이 동일한 Image를 전부 조회합니다.
     */
    List<DockerImage> findPullImageByInfo(Simulation simulation, ImageMeta meta, boolean allTags);


	/*
	image push
	 */

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

    /**
     * Local에서 댕글링 이미지를 조회합니다.
     *
     * @param simulation Image가 속한 simulation
     */
    List<DockerImage> findDanglingImageBySimulationInLocal(Simulation simulation);

    /**
     * Local에서 참조되지 않는 Image를 조회합니다.
     *
     * @param simulation Image가 속한 simulation
     */
    List<DockerImage> findUnreferencedImageBySimulationInLocal(Simulation simulation);

    DockerImage findByIdentifierAndLocation(Simulation simulation, String nameOrShortHexId,
                                            ImageLocation location);
}

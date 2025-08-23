package com.dockersim.service.image;


import com.dockersim.dto.response.DockerImageResponse;
import com.dockersim.dto.response.ImageListResponse;
import com.dockersim.dto.response.ImageRemoveResponse;
import java.util.List;

public interface DockerImageService {

    /**
     * 원격 저장소에서 이미지를 다운로드하는 동작을 시뮬레이션합니다.
     *
     * @param imageName 다운로드할 이미지 이름
     * @return 다운로드한 이미지 정보
     */
    DockerImageResponse pullImage(String imageName);

    /**
     * 로컬 이미지를 원격 저장소로 업로드하는 동작을 시뮬레이션합니다.
     *
     * @param imageName 업로드할 이미지 이름
     * @return 업로드한 이미지 정보
     */
    DockerImageResponse pushImage(String imageName);

    /**
     * 로컬에 저장된 이미지 목록을 보여주는 동작을 시뮬레이션합니다.
     *
     * @return 다운로드한 이미지 목록
     */
    ImageListResponse listImages();

    /**
     * 로컬에 저장된 이미지를 삭제하는 동작을 시뮬레이션합니다.
     *
     * @param imageNameOrId 삭제할 이미지 이름 또는 ID
     * @return 삭제 결과
     */
    ImageRemoveResponse removeImage(String imageNameOrId);

    /**
     * Dockerfile로부터 이미지를 빌드하는 동작을 시뮬레이션합니다.
     *
     * @param name 생성할 이미지의 이름과 태그
     * @return 처리 결과 문자열
     */
    String buildImage(String name);

    /**
     * 사용하지 않는 이미지를 정리하는 동작을 시뮬레이션합니다.
     *
     * @return 삭제된 이미지들의 ID
     */
    List<String> pruneImages();

    /**
     * 이미지의 상세 정보를 보여주는 동작을 시뮬레이션합니다.
     *
     * @param imageName 정보를 확인할 이미지 이름 또는 ID
     * @return JSON 형태의 상세 정보 문자열
     */
    String inspectImage(String imageName);

}

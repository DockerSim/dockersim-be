package com.dockersim.service.image;


import com.dockersim.dto.response.DockerImageResponse;
import java.util.List;

/**
 * Docker 이미지 관련 모든 시뮬레이션 비즈니스 로직을 정의하는 인터페이스
 */
public interface DockerImageService {

    /**
     * 원격 저장소에서 이미지를 다운로드하는 동작을 시뮬레이션합니다.
     *
     * @param imageName 다운로드할 이미지 이름
     * @return 다운로드한 이미지 정보
     */
    DockerImageResponse pullImage(String imageName);

    /**
     * 로컬에 저장된 이미지 목록을 보여주는 동작을 시뮬레이션합니다.
     *
     * @return 다운로드한 이미지 목록
     */
    List<DockerImageResponse> listImages();

    /**
     * 로컬에 저장된 이미지를 삭제하는 동작을 시뮬레이션합니다.
     *
     * @param imageName 삭제할 이미지 이름 또는 ID
     * @return 삭제된 이미지의 ID
     */
    String removeImage(String imageName);

    /**
     * 250812 검토 필요
     * <p>
     * <p>
     * Dockerfile로부터 이미지를 빌드하는 동작을 시뮬레이션합니다.
     *
     * @param tag  생성할 이미지의 이름과 태그
     * @param path Dockerfile이 위치한 경로
     * @return 처리 결과 문자열
     */
    String buildImage(String tag, String path);

    /**
     * 사용하지 않는 이미지를 정리하는 동작을 시뮬레이션합니다.
     *
     * @param all 모든 이미지를 대상으로 할지 여부
     * @return 삭제된 이미지들의 ID
     */
    List<String> pruneImages(boolean all);

    /**
     * 250812 검토 필요
     * <p>
     * 이미지의 상세 정보를 보여주는 동작을 시뮬레이션합니다.
     *
     * @param imageName 정보를 확인할 이미지 이름 또는 ID
     * @return JSON 형태의 상세 정보 문자열
     */
    String inspectImage(String imageName);

    /**
     * 250812 검토 필요
     * <p>
     * 이미지의 생성 내역(히스토리)을 보여주는 동작을 시뮬레이션합니다.
     *
     * @param imageName 히스토리를 확인할 이미지 이름 또는 ID
     * @return 히스토리 목록 형태의 문자열
     */
    String showImageHistory(String imageName);
}

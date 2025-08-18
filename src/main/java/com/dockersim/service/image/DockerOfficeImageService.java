package com.dockersim.service.image;

import com.dockersim.domain.DockerOfficeImage;
import com.dockersim.dto.response.DockerOfficeImageResponse;
import java.util.List;

public interface DockerOfficeImageService {

    /**
     * 미리 파싱된 Docker 공식 이미지 정보들을 DB에 저장합니다.
     */
    void loadAllFromJson();


    /**
     * 도커 이미지의 repository name과 tag가 일치하는 이미지를 찾습니다.
     *
     * @param repositoryName 도커 이미지의 repository name
     * @param tag            도커 이미지의 tag
     * @return 다른 서비스 계층에서 활용할 수 있도록 엔티티를 반환합니다.
     */
    DockerOfficeImage findByNameAndTag(String repositoryName, String tag);

    /**
     * 도커 이미지의 repository name과 일치하는 이미지를 전부찾습니다.
     *
     * @param repositoryName 태그가 생략된 도커 이미지의 repository name
     * @return 응답 형식의 결과를 반환합니다.
     */
    List<DockerOfficeImageResponse> findAllByName(String repositoryName);

    /**
     * 도커 공식 이미지 목록을 반환합니다.
     *
     * @param offset 시작 위치 (페이지네이션 용도)
     * @param limit  반환할 이미지의 최대 개수 (페이지네이션 용도)
     * @return 도커 공식 이미지 목록을 응답 형식으로 반환합니다.
     */
    List<DockerOfficeImageResponse> getAllImages(int offset, int limit);
}
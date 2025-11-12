package com.dockersim.service.image;

import com.dockersim.dto.response.DockerOfficeImageResponse;
import java.util.List;
import java.util.Optional; // Import Optional

public interface DockerOfficeImageService {

    /**
     * 미리 파싱된 Docker 공식 이미지 정보들을 DB에 저장합니다.
     */
    void loadAllFromJson();


    /**
     * name과 tag가 일치하는 도커 공식 Image를 조회합니다.
     *
     * @param name Image 이름
     * @param tag  Image 태그
     */
    Optional<DockerOfficeImageResponse> findByNameAndTag(String name, String tag); // Changed return type to Optional

    /**
     * name이 일치하는 모든 도커 공식 Image를 조회합니다.
     *
     * @param name Image 이름
     */
    List<DockerOfficeImageResponse> findAllByName(String name);

    /**
     * 도커 공식 이미지 목록을 반환합니다.
     */
    List<DockerOfficeImageResponse> getAllImages();
}

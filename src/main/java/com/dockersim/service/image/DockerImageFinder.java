package com.dockersim.service.image;

import com.dockersim.domain.DockerImage;
import com.dockersim.domain.ImageLocation;
import com.dockersim.domain.Simulation;
import java.util.List;
import java.util.Map;

public interface DockerImageFinder {

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

    /**
     * 이미지 이름 문자열(예: 'nginx:latest')을 파싱하여 레포지토리와 태그로 분리합니다.
     *
     * @param name 파싱할 이미지 이름
     * @return 'repository'와 'tag' 키를 가진 Map
     */
    Map<String, String> parserImageName(String name);

}

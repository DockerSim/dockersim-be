package com.dockersim.service.container;

import com.dockersim.domain.ContainerStatus;

public interface ContainerFinder {

    /**
     * 특정 이미지 ID를 기반으로 하며 특정 상태에 있는 컨테이너가 존재하는지 확인합니다.
     *
     * @param imageId 확인할 기반 이미지의 ID
     * @param status  확인할 컨테이너 상태
     * @return 존재하면 true, 그렇지 않으면 false
     */
    boolean existsByImageIdAndStatus(String imageId, ContainerStatus status);
}

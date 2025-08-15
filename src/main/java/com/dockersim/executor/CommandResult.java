package com.dockersim.executor;

import com.dockersim.dto.response.DockerContainerResponse;
import com.dockersim.dto.response.DockerImageResponse;
import com.dockersim.dto.response.DockerNetworkResponse;
import com.dockersim.dto.response.DockerVolumeResponse;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;


/**
 * Docker 명령어 실행 결과
 */
@Getter
@Builder
public class CommandResult {

    /**
     * 콘솔 출력 내용
     */
    private final List<String> console;

    /**
     * 상태 변화 정보
     */
    @Singular
    private List<DockerImageResponse> changedImages;

    @Singular
    private List<DockerContainerResponse> changedContainers;

    @Singular
    private List<DockerVolumeResponse> changedVolumes;

    @Singular
    private List<DockerNetworkResponse> changedNetworks;
}
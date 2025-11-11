package com.dockersim.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;


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
    private CommandResultStatus status;

    /**
     * 명령어 실행 성공 여부
     */
    @Builder.Default
    private boolean success = true; // Add this field with a default value

    @Singular
    private List<DockerImageResponse> changedImages;

    @Singular
    private List<DockerContainerResponse> changedContainers;

    @Singular
    private List<DockerVolumeResponse> changedVolumes;

    @Singular
    private List<DockerNetworkResponse> changedNetworks;
}

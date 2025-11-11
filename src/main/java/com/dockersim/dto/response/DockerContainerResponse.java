package com.dockersim.dto.response;

import com.dockersim.domain.ContainerStatus;
import com.dockersim.domain.DockerContainer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DockerContainerResponse {

    private List<String> console;

    private Long id;
    private String hexId;
    private String shortHexId;
    private String name;
    private String baseImageName;
    private ContainerStatus status;
    private List<String> layer;
    private List<ContainerVolumeInfo> volumes;
    private List<String> networks;

    public static DockerContainerResponse from(List<String> console, DockerContainer container) {
        // 컨테이너에 연결된 볼륨 정보 변환
        List<ContainerVolumeInfo> volumeInfos = container.getContainerVolumes().stream()
                .map(cv -> ContainerVolumeInfo.builder()
                        .volumeName(cv.getVolume().getName())
                        .containerPath(cv.getContainerPath())
                        .isReadOnly(cv.isReadOnly())
                        .build())
                .collect(Collectors.toList());

        // 컨테이너에 연결된 네트워크 정보 변환
        List<String> networkNames = container.getContainerNetworks().stream()
                .map(cn -> cn.getNetwork().getName())
                .collect(Collectors.toList());

        return DockerContainerResponse.builder()
                .console(console)
                .id(container.getId())
                .hexId(container.getHexId())
                .shortHexId(container.getShortHexId())
                .name(container.getName())
                .baseImageName(container.getBaseImage().getName())
                .layer(container.getBaseImage().getLayers())
                .status(container.getStatus())
                .volumes(volumeInfos)
                .networks(networkNames)
                .build();
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ContainerVolumeInfo {
        private String volumeName;
        private String containerPath;
        private boolean isReadOnly;
    }
}

package com.dockersim.dto.response;

import com.dockersim.entity.ContainerSimulation;
import com.dockersim.entity.enums.ContainerStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContainerSimulationDto {

    private Long id;
    private String simulationId;
    private String name;
    private String imageName;
    private String imageTag;
    private ContainerStatus status;
    private Map<String, Object> ports;
    private Map<String, String> environment;
    private Map<String, Object> volumes;
    private Map<String, Object> networks;
    private String command;
    private String workingDir;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime stoppedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public static ContainerSimulationDto from(ContainerSimulation entity) {
        return ContainerSimulationDto.builder()
                .id(entity.getId())
                .simulationId(entity.getSimulationId())
                .name(entity.getName())
                .imageName(entity.getImageName())
                .imageTag(entity.getImageTag())
                .status(entity.getStatus())
                .command(entity.getCommand())
                .workingDir(entity.getWorkingDir())
                .startedAt(entity.getStartedAt())
                .stoppedAt(entity.getStoppedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
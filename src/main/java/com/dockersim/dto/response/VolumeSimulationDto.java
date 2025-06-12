package com.dockersim.dto.response;

import com.dockersim.entity.VolumeSimulation;
import com.dockersim.entity.enums.MountType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "볼륨 정보")
public class VolumeSimulationDto {

    @Schema(description = "볼륨 ID")
    private Long id;

    @Schema(description = "시뮬레이션 ID")
    private String simulationId;

    @Schema(description = "볼륨 이름", example = "my-volume")
    private String name;

    @Schema(description = "드라이버", example = "local")
    private String driver;

    @Schema(description = "마운트 포인트", example = "/var/lib/docker/volumes/my-volume")
    private String mountPoint;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "수정 시간")
    private LocalDateTime updatedAt;

    public static VolumeSimulationDto from(VolumeSimulation entity) {
        return VolumeSimulationDto.builder()
                .id(entity.getId())
                .simulationId(entity.getSimulationId())
                .name(entity.getName())
                .driver(entity.getDriver())
                .mountPoint(entity.getSource())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
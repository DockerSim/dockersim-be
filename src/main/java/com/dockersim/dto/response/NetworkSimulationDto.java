package com.dockersim.dto.response;

import com.dockersim.entity.NetworkSimulation;
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
@Schema(description = "네트워크 정보")
public class NetworkSimulationDto {

    @Schema(description = "네트워크 ID")
    private Long id;

    @Schema(description = "시뮬레이션 ID")
    private String simulationId;

    @Schema(description = "네트워크 이름", example = "bridge")
    private String name;

    @Schema(description = "드라이버", example = "bridge")
    private String driver;

    @Schema(description = "서브넷", example = "172.17.0.0/16")
    private String subnet;

    @Schema(description = "게이트웨이", example = "172.17.0.1")
    private String gateway;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "수정 시간")
    private LocalDateTime updatedAt;

    public static NetworkSimulationDto from(NetworkSimulation entity) {
        return NetworkSimulationDto.builder()
                .id(entity.getId())
                .simulationId(entity.getSimulationId())
                .name(entity.getName())
                .driver(entity.getDriver())
                .subnet(entity.getSubnet())
                .gateway(entity.getGateway())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
package com.dockersim.dto.response;

import com.dockersim.domain.Simulation;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TDD Green 단계 2: 시뮬레이션 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulationResponse {

    private String simulationId;
    private String title;
    private String shareState;
    private String ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SimulationResponse from(Simulation simulation) {
        return SimulationResponse.builder()
            .simulationId(simulation.getSimulationId())
            .title(simulation.getTitle())
            .shareState(simulation.getShareState().name())
            .ownerId(simulation.getOwner().getUserId())
            .createdAt(simulation.getCreatedAt())
            .updatedAt(simulation.getUpdatedAt())
            .build();
    }
}
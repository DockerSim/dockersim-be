package com.dockersim.dto.response;

import com.dockersim.domain.Simulation;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulationResponse {

    private String simulationPublicId;
    private String title;
    private String shareState;
    private String ownerPublicId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SimulationResponse from(Simulation simulation) {
        return SimulationResponse.builder()
            .simulationPublicId(simulation.getPublicId())
            .title(simulation.getTitle())
            .shareState(simulation.getShareState().name())
            .ownerPublicId(simulation.getOwner().getPublicId())
            .createdAt(simulation.getCreatedAt())
            .updatedAt(simulation.getUpdatedAt())
            .build();
    }
}
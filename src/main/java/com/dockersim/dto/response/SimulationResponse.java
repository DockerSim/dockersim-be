package com.dockersim.dto.response;

import com.dockersim.domain.Simulation;
import com.dockersim.util.IdConverter;
import java.time.LocalDateTime;
import java.util.UUID;
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

    private UUID id;
    private String title;
    private String shareState;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SimulationResponse from(Simulation simulation) {
        return SimulationResponse.builder()
            .id(IdConverter.toUUID(simulation.getId()))
            .title(simulation.getTitle())
            .shareState(simulation.getShareState().name())
            .userId(simulation.getUser().getId())
            .createdAt(simulation.getCreatedAt())
            .updatedAt(simulation.getUpdatedAt())
            .build();
    }
}
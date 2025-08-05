package com.dockersim.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * TDD Green 단계 2: 시뮬레이션 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimulationResponse {
    private Long id;
    private String title;
    private String shareState;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
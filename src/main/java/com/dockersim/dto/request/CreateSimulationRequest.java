package com.dockersim.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TDD Green 단계: 시뮬레이션 생성 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSimulationRequest {
    private String title;
    private String shareState;
    private Long userId;
}
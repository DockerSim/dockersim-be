package com.dockersim.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 시뮬레이션 공유 상태 변경 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateShareStateRequest {
    private String shareState; // PRIVATE, READ, WRITE
}
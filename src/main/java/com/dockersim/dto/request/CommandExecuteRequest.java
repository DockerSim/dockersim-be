package com.dockersim.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Docker 명령어 실행 요청")
public class CommandExecuteRequest {
    @Schema(description = "실행할 Docker 명령어", example = "docker ps", required = true)
    private String command;
    @Schema(description = "시뮬레이션 ID", example = "sim_123456")
    private String simulationId;
    @Schema(description = "사용자 ID", example = "1")
    private Long userId;
    @Schema(description = "협업 세션 ID", example = "session_abc123")
    private String sessionId; // 협업 세션용
}
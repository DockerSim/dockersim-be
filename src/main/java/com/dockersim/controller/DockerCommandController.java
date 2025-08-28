package com.dockersim.controller;

import com.dockersim.common.ApiResponse;
import com.dockersim.config.SimulationUserPrincipal;
import com.dockersim.config.auth.CurrentUser;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.exception.code.DockerCommandErrorCode;
import com.dockersim.service.command.CommandExecutorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Docker 명령어 실행 API 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "도커 명령어 API", description = "도커 명령어 API")
public class DockerCommandController {

    private final CommandExecutorService commandExecutor;

    /**
     * Docker 명령어 실행 API
     *
     * @param principal 시뮬레이션을 조작핧 사용자와 시뮬레이션 인증 정보
     * @param command   실행할 도커 명령어
     * @return 도커 명령어에 의한 상태 변화 응답
     */
    @Operation(summary = "Docker 명령어 실행",
        description = "시뮬레이션에서 Docker 명령어를 실행 후 상태 변화를 응답합니다. ")
    @PostMapping("/simulations/{simulationPublicId}/command")
    public ResponseEntity<ApiResponse<CommandResult>> executeCommand(
        @Parameter(description = "시뮬레이션을 조작핧 사용자와 시뮬레이션 인증 정보", required = true, hidden = true)
        @CurrentUser SimulationUserPrincipal principal,
        
        @Parameter(description = "실행할 도커 명령어", required = true) @RequestBody String command
    ) {

        CommandResult result = commandExecutor.execute(command, principal);
        if (result == null) {
            return ResponseEntity.ok(
                ApiResponse.error(DockerCommandErrorCode.FAILED_EXECUTE_DOCKER_COMMAND));
        }
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
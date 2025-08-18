package com.dockersim.controller;

import com.dockersim.common.ApiResponse;
import com.dockersim.context.SimulationContextHolder;
import com.dockersim.dto.response.CommandResult;
import com.dockersim.exception.code.DockerCommandErrorCode;
import com.dockersim.service.command.CommandExecutorService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
public class DockerCommandController {

    private static final Set<String> KNOWN_BUT_UNSUPPORTED = Set.of(
        "swarm", "stack", "node", "service", "secret", "config", "context"
    );

    private final CommandExecutorService commandExecutor;

    /**
     * Docker 명령어 실행 API
     */
    @PostMapping("/simulations/{simulationId}/command")
    public ResponseEntity<ApiResponse<CommandResult>> executeCommand(
        @PathVariable String simulationId,
        @RequestBody String command
        // @AuthenticationPrincipal Long userId
    ) {
        SimulationContextHolder.setSimulationId(simulationId);

        CommandResult result = commandExecutor.execute(command);
        if (result == null) {
            return ResponseEntity.ok(
                ApiResponse.error(DockerCommandErrorCode.FAILED_EXECUTE_DOCKER_COMMAND));
        }
        return ResponseEntity.ok(ApiResponse.success(result));
    }


    /**
     * 지원하는 Docker 명령어 목록 조회 API
     */
    @GetMapping("/help")
    public ResponseEntity<ApiResponse<String[]>> getSupportedCommands() {
        log.info("지원 명령어 목록 조회 요청");

        String[] supportedCommands = {
            "run", "ps", "start", "stop", "restart",
            "rm", "logs", "inspect", "images", "pull", "rmi"
        };

        ApiResponse<String[]> response = ApiResponse.success(null);

        return ResponseEntity.ok(response);
    }
}
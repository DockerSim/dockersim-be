package com.dockersim.controller;

import com.dockersim.exception.code.DockerCommandErrorCode;
import com.dockersim.executor.CommandResult;
import com.dockersim.service.command.CommandExecutorService;
import com.dockersim.web.ApiResponse;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
//    @PostMapping("/execute")
//    public ResponseEntity<ApiResponse<CommandResult>> executeCommand(
//        @RequestBody DockerCommandRequest request) {
//
//        String command = request.getCommand();
//        Long simulationId = request.getSimulationId();
//
//        log.info("Docker 명령어 실행 요청: command={}, simulationId={}", command, simulationId);
//
//        try {
//            // 1. 명령어 파싱
//            ParsedDockerCommand parsedCommand = dockerCommandParser.parse(command);
//
//            // 2. 명령어 실행
//            CommandResult result = commandExecutor.execute(parsedCommand, simulationId);
//
//            ApiResponse<CommandResult> apiResponse = ApiResponse.ok(result);
//
//            return ResponseEntity.ok(apiResponse);
//
//        } catch (Exception e) {
//            log.error("Docker 명령어 실행 실패: {}", e.getMessage(), e);
//            return ResponseEntity.ok(null);
//        }
//    }

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

        ApiResponse<String[]> response = ApiResponse.ok(null);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/command")
    public ResponseEntity<ApiResponse<CommandResult>> handleCommand(
        @RequestBody String command
    ) {
        CommandResult result = commandExecutor.execute(command);
        if (result == null) {
            return ResponseEntity.ok(
                ApiResponse.error(DockerCommandErrorCode.FAILED_EXECUTE_DOCKER_COMMAND));
        }
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

}
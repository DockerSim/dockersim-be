package com.dockersim.controller;

import com.dockersim.dto.request.DockerCommandRequest;
import com.dockersim.executor.CommandExecuteResult;
import com.dockersim.executor.CommandExecutor;
import com.dockersim.parser.DockerCommandParser;
import com.dockersim.parser.ParsedDockerCommand;
import com.dockersim.web.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Docker 명령어 실행 API 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/docker")
@RequiredArgsConstructor
public class DockerCommandController {

        private final CommandExecutor commandExecutor;
        private final DockerCommandParser dockerCommandParser;

        /**
         * Docker 명령어 실행 API
         */
        @PostMapping("/execute")
        public ResponseEntity<ApiResponse<CommandExecuteResult>> executeCommand(
                        @RequestBody DockerCommandRequest request) {

                String command = request.getCommand();
                Long simulationId = request.getSimulationId();

                log.info("Docker 명령어 실행 요청: command={}, simulationId={}", command, simulationId);

                try {
                        // 1. 명령어 파싱
                        ParsedDockerCommand parsedCommand = dockerCommandParser.parse(command);

                        // 2. 명령어 실행
                        CommandExecuteResult result = commandExecutor.execute(parsedCommand, simulationId);

                        ApiResponse<CommandExecuteResult> apiResponse = ApiResponse.ok(result);

                        return ResponseEntity.ok(apiResponse);

                } catch (Exception e) {
                        log.error("Docker 명령어 실행 실패: {}", e.getMessage(), e);
                        return ResponseEntity.ok(null);
                }
        }

        /**
         * 지원하는 Docker 명령어 목록 조회 API
         */
        @GetMapping("/commands")
        public ResponseEntity<ApiResponse<String[]>> getSupportedCommands() {
                log.info("지원 명령어 목록 조회 요청");

                String[] supportedCommands = {
                                "run", "ps", "start", "stop", "restart",
                                "rm", "logs", "inspect", "images", "pull", "rmi"
                };

                ApiResponse<String[]> response = ApiResponse.ok(null);

                return ResponseEntity.ok(response);
        }
}
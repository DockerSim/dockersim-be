// 이 클래스는 Docker 명령어 실행 요청을 처리하는 컨트롤러입니다.
// 주요 메서드:
// - executeCommand : Docker 명령어 실행 API
// - getCommandHistory : 명령어 실행 이력 조회 API

package com.dockersim.controller;

import com.dockersim.dto.CommandExecuteResult;
import com.dockersim.dto.CommandRequest;
import com.dockersim.dto.CommandResponse;
import com.dockersim.repository.CommandHistoryRepository;
import com.dockersim.executor.CommandExecutor;
import com.dockersim.parser.DockerCommandParser;
import com.dockersim.parser.ParsedDockerCommand;
import com.dockersim.domain.CommandHistory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/docker")
@Tag(name = "Docker Command API", description = "Docker 명령어 시뮬레이션 API")
@CrossOrigin(origins = "*")
public class DockerCommandController {

    private final DockerCommandParser parser;
    private final CommandExecutor executor;
    private final CommandHistoryRepository commandHistoryRepository;

    public DockerCommandController(
            DockerCommandParser parser,
            CommandExecutor executor,
            CommandHistoryRepository commandHistoryRepository) {
        this.parser = parser;
        this.executor = executor;
        this.commandHistoryRepository = commandHistoryRepository;
    }

    @PostMapping("/execute")
    @Operation(summary = "Docker 명령어 실행", description = "Docker 명령어를 파싱하고 실행합니다.")
    public ResponseEntity<CommandResponse> executeCommand(@RequestBody CommandRequest request) {
        try {
            // 명령어 파싱
            ParsedDockerCommand parsedCommand = parser.parse(request.getCommand());

            // 명령어 실행
            CommandExecuteResult result = executor.execute(parsedCommand);

            // 명령어 이력 저장
            CommandHistory history = new CommandHistory(
                    request.getCommand(),
                    result.isSuccess(),
                    result.getMessage(),
                    result.getResourceId());
            commandHistoryRepository.save(history);

            // 응답 생성
            CommandResponse response = new CommandResponse(
                    result.isSuccess(),
                    result.getMessage(),
                    result.getResourceId(),
                    parsedCommand);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            // 파싱 오류 처리
            CommandHistory history = new CommandHistory(
                    request.getCommand(),
                    false,
                    "명령어 파싱 오류: " + e.getMessage(),
                    null);
            commandHistoryRepository.save(history);

            CommandResponse response = new CommandResponse(
                    false,
                    "명령어 파싱 오류: " + e.getMessage(),
                    null,
                    null);

            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            // 기타 오류 처리
            CommandHistory history = new CommandHistory(
                    request.getCommand(),
                    false,
                    "시스템 오류: " + e.getMessage(),
                    null);
            commandHistoryRepository.save(history);

            CommandResponse response = new CommandResponse(
                    false,
                    "시스템 오류: " + e.getMessage(),
                    null,
                    null);

            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/history")
    @Operation(summary = "명령어 실행 이력 조회", description = "최근 실행된 Docker 명령어 이력을 조회합니다.")
    public ResponseEntity<List<CommandHistory>> getCommandHistory(
            @RequestParam(defaultValue = "20") int limit) {
        try {
            List<CommandHistory> history = commandHistoryRepository.findAllOrderByExecutedAtDesc();

            // 제한된 개수만 반환
            if (history.size() > limit) {
                history = history.subList(0, limit);
            }

            return ResponseEntity.ok(history);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/health")
    @Operation(summary = "서비스 상태 확인", description = "Docker 시뮬레이터 서비스의 상태를 확인합니다.")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Docker Simulator is running!");
    }
}
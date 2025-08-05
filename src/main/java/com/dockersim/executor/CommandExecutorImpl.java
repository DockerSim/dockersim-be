package com.dockersim.executor;

import com.dockersim.domain.*;
import com.dockersim.parser.DockerCommandParser;
import com.dockersim.parser.ParsedDockerCommand;
import com.dockersim.repository.SimulationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Docker 명령어 실행 엔진 구현체 (라우터 역할)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommandExecutorImpl implements CommandExecutor {

    private final SimulationRepository simulationRepository;
    private final DockerCommandParser parser;
    private final ContainerService containerService;
    private final ImageService imageService;

    @Override
    public CommandExecuteResult execute(ParsedDockerCommand parsedCommand, Long simulationId) {
        try {
            log.info("명령어 실행: {} (시뮬레이션 ID: {})", parsedCommand.getOriginalCommand(), simulationId);

            // 1. 시뮬레이션 존재 확인
            Simulation simulation = simulationRepository.findById(simulationId)
                    .orElseThrow(() -> new RuntimeException("시뮬레이션을 찾을 수 없습니다: " + simulationId));

            // 2. 명령어별 실행
            return executeCommand(parsedCommand, simulation);

        } catch (Exception e) {
            log.error("명령어 실행 실패: {}", e.getMessage(), e);
            return createErrorResult(parsedCommand.getOriginalCommand(), e.getMessage());
        }
    }

    @Override
    public boolean canExecute(String command) {
        return switch (command) {
            case "run", "ps", "start", "stop", "restart", "rm", "logs", "inspect", "images", "pull", "rmi" -> true;
            default -> false;
        };
    }

    /**
     * 파싱된 명령어를 적절한 서비스로 라우팅
     */
    private CommandExecuteResult executeCommand(ParsedDockerCommand command, Simulation simulation) {
        return switch (command.getCommand()) {
            case "run" -> executeRun(command, simulation);
            case "ps" -> containerService.executePs(command, simulation);
            case "start" -> containerService.executeStart(command, simulation);
            case "stop" -> containerService.executeStop(command, simulation);
            case "restart" -> containerService.executeRestart(command, simulation);
            case "rm" -> containerService.executeRm(command, simulation);
            case "logs" -> containerService.executeLogs(command, simulation);
            case "inspect" -> containerService.executeInspect(command, simulation);
            case "images" -> imageService.executeImages(command, simulation);
            case "pull" -> imageService.executePull(command, simulation);
            case "rmi" -> imageService.executeRmi(command, simulation);
            default -> createErrorResult(command.getOriginalCommand(), "지원하지 않는 명령어입니다: " + command.getCommand());
        };
    }

    /**
     * docker run 명령어는 컨테이너와 이미지 서비스를 모두 사용하므로 여기서 처리
     */
    private CommandExecuteResult executeRun(ParsedDockerCommand command, Simulation simulation) {
        String imageName = command.getTarget();
        if (imageName == null) {
            throw new RuntimeException("이미지 이름이 필요합니다");
        }

        // 1. 이미지 존재 확인 및 필요시 pull 시뮬레이션
        boolean imageExisted = imageService.imageExists(imageName, simulation);
        Image image = imageService.ensureImageExists(imageName, simulation);
        boolean imagePulled = !imageExisted;

        // 2. 컨테이너 생성 및 실행
        return containerService.executeRun(command, simulation, image, imagePulled);
    }

    /**
     * 에러 결과 생성
     */
    private CommandExecuteResult createErrorResult(String command, String errorMessage) {
        return CommandExecuteResult.builder()
                .command(command)
                .success(false)
                .errorMessage(errorMessage)
                .console(ConsoleOutput.builder()
                        .output(java.util.Arrays.asList("Error: " + errorMessage))
                        .success(false)
                        .build())
                .stateChanges(createEmptyStateChanges())
                .build();
    }

    private StateChanges createEmptyStateChanges() {
        return StateChanges.builder()
                .containers(ResourceChanges.builder()
                        .added(java.util.Collections.emptyList())
                        .modified(java.util.Collections.emptyList())
                        .removed(java.util.Collections.emptyList())
                        .build())
                .images(ResourceChanges.builder()
                        .added(java.util.Collections.emptyList())
                        .modified(java.util.Collections.emptyList())
                        .removed(java.util.Collections.emptyList())
                        .build())
                .networks(ResourceChanges.builder()
                        .added(java.util.Collections.emptyList())
                        .modified(java.util.Collections.emptyList())
                        .removed(java.util.Collections.emptyList())
                        .build())
                .volumes(ResourceChanges.builder()
                        .added(java.util.Collections.emptyList())
                        .modified(java.util.Collections.emptyList())
                        .removed(java.util.Collections.emptyList())
                        .build())
                .build();
    }
}
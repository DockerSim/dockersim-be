package com.dockersim.executor;

import com.dockersim.domain.*;
import com.dockersim.parser.ParsedDockerCommand;
import com.dockersim.repository.ContainerRepository;
import com.dockersim.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 컨테이너 관련 비즈니스 로직 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ContainerService {

    private final ContainerRepository containerRepository;
    private final ImageRepository imageRepository;

    /**
     * docker run 명령어 실행
     */
    public CommandExecuteResult executeRun(ParsedDockerCommand command, Simulation simulation, Image image,
            boolean imagePulled) {
        String imageName = command.getTarget();

        // 컨테이너 이름 결정
        String containerName = command.getOptionValue("name");
        if (containerName == null) {
            containerName = generateContainerName(imageName);
        } else {
            // 이름 중복 확인
            if (containerRepository.findBySimulationIdAndName(simulation.getId(), containerName).isPresent()) {
                throw new RuntimeException("컨테이너 이름이 이미 사용 중입니다: " + containerName);
            }
        }

        // 컨테이너 생성
        String containerId = generateContainerId();
        Container container = Container.builder()
                .containerId(containerId)
                .name(containerName)
                .image(imageName)
                .status(ContainerStatus.RUNNING)
                .simulation(simulation)
                .build();

        // 옵션 처리
        processRunOptions(container, command);

        // 저장
        containerRepository.save(container);

        return createRunResult(command, container, imagePulled, simulation, image);
    }

    /**
     * docker ps 명령어 실행
     */
    public CommandExecuteResult executePs(ParsedDockerCommand command, Simulation simulation) {
        List<Container> containers;

        if (command.isFlagSet("a") || command.isFlagSet("all")) {
            containers = containerRepository.findBySimulationId(simulation.getId());
        } else {
            containers = containerRepository.findBySimulationIdAndStatus(simulation.getId(), ContainerStatus.RUNNING);
        }

        return createPsResult(command, containers, simulation);
    }

    /**
     * docker start 명령어 실행
     */
    public CommandExecuteResult executeStart(ParsedDockerCommand command, Simulation simulation) {
        String containerNameOrId = command.getTarget();
        if (containerNameOrId == null) {
            throw new RuntimeException("컨테이너 이름 또는 ID가 필요합니다");
        }

        Container container = findContainerByNameOrId(containerNameOrId, simulation);
        if (container == null) {
            throw new RuntimeException("컨테이너를 찾을 수 없습니다: " + containerNameOrId);
        }

        if (container.getStatus() == ContainerStatus.RUNNING) {
            throw new RuntimeException("컨테이너가 이미 실행 중입니다: " + containerNameOrId);
        }

        container.start();
        containerRepository.save(container);

        return createStartResult(command, container, simulation);
    }

    /**
     * docker stop 명령어 실행
     */
    public CommandExecuteResult executeStop(ParsedDockerCommand command, Simulation simulation) {
        String containerNameOrId = command.getTarget();
        if (containerNameOrId == null) {
            throw new RuntimeException("컨테이너 이름 또는 ID가 필요합니다");
        }

        Container container = findContainerByNameOrId(containerNameOrId, simulation);
        if (container == null) {
            throw new RuntimeException("컨테이너를 찾을 수 없습니다: " + containerNameOrId);
        }

        if (container.getStatus() != ContainerStatus.RUNNING) {
            throw new RuntimeException("컨테이너가 실행 중이 아닙니다: " + containerNameOrId);
        }

        container.stop();
        containerRepository.save(container);

        return createStopResult(command, container, simulation);
    }

    /**
     * docker restart 명령어 실행
     */
    public CommandExecuteResult executeRestart(ParsedDockerCommand command, Simulation simulation) {
        String containerNameOrId = command.getTarget();
        if (containerNameOrId == null) {
            throw new RuntimeException("컨테이너 이름 또는 ID가 필요합니다");
        }

        Container container = findContainerByNameOrId(containerNameOrId, simulation);
        if (container == null) {
            throw new RuntimeException("컨테이너를 찾을 수 없습니다: " + containerNameOrId);
        }

        container.restart();
        containerRepository.save(container);

        return createRestartResult(command, container, simulation);
    }

    /**
     * docker rm 명령어 실행
     */
    public CommandExecuteResult executeRm(ParsedDockerCommand command, Simulation simulation) {
        String containerNameOrId = command.getTarget();
        if (containerNameOrId == null) {
            throw new RuntimeException("컨테이너 이름 또는 ID가 필요합니다");
        }

        Container container = findContainerByNameOrId(containerNameOrId, simulation);
        if (container == null) {
            throw new RuntimeException("컨테이너를 찾을 수 없습니다: " + containerNameOrId);
        }

        if (container.isRunning() && !command.isFlagSet("f") && !command.isFlagSet("force")) {
            throw new RuntimeException("실행 중인 컨테이너는 삭제할 수 없습니다. 먼저 중지하거나 -f 옵션을 사용하세요: " + containerNameOrId);
        }

        containerRepository.delete(container);

        return createRmResult(command, container, simulation);
    }

    /**
     * docker logs 명령어 실행
     */
    public CommandExecuteResult executeLogs(ParsedDockerCommand command, Simulation simulation) {
        String containerNameOrId = command.getTarget();
        if (containerNameOrId == null) {
            throw new RuntimeException("컨테이너 이름 또는 ID가 필요합니다");
        }

        Container container = findContainerByNameOrId(containerNameOrId, simulation);
        if (container == null) {
            throw new RuntimeException("컨테이너를 찾을 수 없습니다: " + containerNameOrId);
        }

        return createLogsResult(command, container, simulation);
    }

    /**
     * docker inspect 명령어 실행
     */
    public CommandExecuteResult executeInspect(ParsedDockerCommand command, Simulation simulation) {
        String containerNameOrId = command.getTarget();
        if (containerNameOrId == null) {
            throw new RuntimeException("컨테이너 이름 또는 ID가 필요합니다");
        }

        Container container = findContainerByNameOrId(containerNameOrId, simulation);
        if (container == null) {
            throw new RuntimeException("컨테이너를 찾을 수 없습니다: " + containerNameOrId);
        }

        return createInspectResult(command, container, simulation);
    }

    // === Private Helper Methods ===

    private Container findContainerByNameOrId(String nameOrId, Simulation simulation) {
        // 먼저 이름으로 찾기
        Optional<Container> containerByName = containerRepository
                .findBySimulationIdAndName(simulation.getId(), nameOrId);
        if (containerByName.isPresent()) {
            return containerByName.get();
        }

        // 이름으로 못 찾으면 ID로 찾기 (short ID 또는 full ID)
        List<Container> containers = containerRepository.findBySimulationId(simulation.getId());
        for (Container container : containers) {
            // Full ID 매치
            if (container.getContainerId().equals(nameOrId)) {
                return container;
            }
            // Short ID 매치 (입력된 것이 컨테이너 ID의 시작 부분과 일치)
            if (container.getContainerId().startsWith(nameOrId) && nameOrId.length() >= 4) {
                return container;
            }
        }

        return null;
    }

    private String generateContainerName(String imageName) {
        String baseName = imageName.split(":")[0].replaceAll("[^a-zA-Z0-9]", "_");
        return baseName + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String generateContainerId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }

    private void processRunOptions(Container container, ParsedDockerCommand command) {
        // 포트 매핑 처리
        String port = command.getOptionValue("p");
        if (port != null) {
            container.addPortMapping(port);
        }

        // 볼륨 마운트 처리
        String volume = command.getOptionValue("v");
        if (volume != null) {
            container.addVolumeMount(volume);
        }

        // 환경 변수 처리
        String env = command.getOptionValue("e");
        if (env != null) {
            container.addEnvironmentVariable(env);
        }
    }

    // === Result Creation Methods ===

    private CommandExecuteResult createRunResult(ParsedDockerCommand command,
            Container container,
            boolean imagePulled,
            Simulation simulation,
            Image image) {

        ConsoleOutput.ConsoleOutputBuilder consoleBuilder = ConsoleOutput.builder()
                .success(true);

        List<String> outputLines = new java.util.ArrayList<>();

        if (imagePulled) {
            // pull 과정 시뮬레이션
            String fullImageName = container.getImage().contains(":") ? container.getImage()
                    : container.getImage() + ":latest";
            String repository = container.getImage().split(":")[0];
            outputLines.add("Unable to find image '" + fullImageName + "' locally");
            outputLines.add("latest: Pulling from library/" + repository);
            outputLines.add("31b3f1ad4ce1: Pull complete");
            outputLines.add("fd42b079d0f8: Pull complete");
            outputLines.add("Status: Downloaded newer image for " + fullImageName);
        }

        outputLines.add(container.getShortContainerId());
        consoleBuilder.output(outputLines).finalLine(container.getShortContainerId());

        // 상태 변화 구성
        StateChanges.StateChangesBuilder stateChangesBuilder = StateChanges.builder();

        stateChangesBuilder.containers(ResourceChanges.builder()
                .added(Arrays.asList(container))
                .modified(Collections.emptyList())
                .removed(Collections.emptyList())
                .build());

        if (imagePulled) {
            stateChangesBuilder.images(ResourceChanges.builder()
                    .added(image != null ? Arrays.asList(image) : Collections.emptyList())
                    .modified(Collections.emptyList())
                    .removed(Collections.emptyList())
                    .build());
        } else {
            stateChangesBuilder.images(ResourceChanges.builder()
                    .added(Collections.emptyList())
                    .modified(Collections.emptyList())
                    .removed(Collections.emptyList())
                    .build());
        }

        stateChangesBuilder.networks(ResourceChanges.builder()
                .added(Collections.emptyList())
                .modified(Collections.emptyList())
                .removed(Collections.emptyList())
                .build());

        stateChangesBuilder.volumes(ResourceChanges.builder()
                .added(Collections.emptyList())
                .modified(Collections.emptyList())
                .removed(Collections.emptyList())
                .build());

        return CommandExecuteResult.builder()
                .command(command.getOriginalCommand())
                .success(true)
                .console(consoleBuilder.build())
                .stateChanges(stateChangesBuilder.build())
                .summary(createSummary(simulation))
                .hints(createRunHints(container))
                .executedAt(LocalDateTime.now())
                .resourceId(container.getContainerId())
                .build();
    }

    private CommandExecuteResult createPsResult(ParsedDockerCommand command,
            List<Container> containers,
            Simulation simulation) {
        List<String> outputLines = new java.util.ArrayList<>();

        // 헤더
        outputLines.add(
                "CONTAINER ID   IMAGE                COMMAND                  CREATED         STATUS         PORTS     NAMES");

        // 컨테이너 목록
        for (Container container : containers) {
            String line = String.format("%-13s   %-20s   \"%-20s\"   %-15s   %-13s   %-9s   %s",
                    container.getShortContainerId(),
                    container.getImage(),
                    "/docker-entrypoint.sh",
                    "2 minutes ago",
                    container.getStatus() == ContainerStatus.RUNNING ? "Up 2 minutes" : "Exited",
                    container.getPortMappings().isEmpty() ? "" : String.join(",", container.getPortMappings()),
                    container.getName());
            outputLines.add(line);
        }

        ConsoleOutput console = ConsoleOutput.builder()
                .output(outputLines)
                .success(true)
                .build();

        StateChanges stateChanges = createEmptyStateChanges();

        return CommandExecuteResult.builder()
                .command(command.getOriginalCommand())
                .success(true)
                .console(console)
                .stateChanges(stateChanges)
                .summary(createSummary(simulation))
                .hints(createPsHints())
                .executedAt(LocalDateTime.now())
                .build();
    }

    private CommandExecuteResult createStartResult(ParsedDockerCommand command, Container container,
            Simulation simulation) {
        ConsoleOutput console = ConsoleOutput.builder()
                .output(Arrays.asList(container.getName()))
                .finalLine(container.getName())
                .success(true)
                .build();

        StateChanges stateChanges = StateChanges.builder()
                .containers(ResourceChanges.builder()
                        .added(Collections.emptyList())
                        .modified(Arrays.asList(container))
                        .removed(Collections.emptyList())
                        .build())
                .images(ResourceChanges.builder()
                        .added(Collections.emptyList())
                        .modified(Collections.emptyList())
                        .removed(Collections.emptyList())
                        .build())
                .networks(ResourceChanges.builder()
                        .added(Collections.emptyList())
                        .modified(Collections.emptyList())
                        .removed(Collections.emptyList())
                        .build())
                .volumes(ResourceChanges.builder()
                        .added(Collections.emptyList())
                        .modified(Collections.emptyList())
                        .removed(Collections.emptyList())
                        .build())
                .build();

        return CommandExecuteResult.builder()
                .command(command.getOriginalCommand())
                .success(true)
                .console(console)
                .stateChanges(stateChanges)
                .summary(createSummary(simulation))
                .hints(createStartHints(container))
                .executedAt(LocalDateTime.now())
                .resourceId(container.getContainerId())
                .build();
    }

    private CommandExecuteResult createStopResult(ParsedDockerCommand command, Container container,
            Simulation simulation) {
        ConsoleOutput console = ConsoleOutput.builder()
                .output(Arrays.asList(container.getName()))
                .finalLine(container.getName())
                .success(true)
                .build();

        StateChanges stateChanges = StateChanges.builder()
                .containers(ResourceChanges.builder()
                        .added(Collections.emptyList())
                        .modified(Arrays.asList(container))
                        .removed(Collections.emptyList())
                        .build())
                .images(ResourceChanges.builder()
                        .added(Collections.emptyList())
                        .modified(Collections.emptyList())
                        .removed(Collections.emptyList())
                        .build())
                .networks(ResourceChanges.builder()
                        .added(Collections.emptyList())
                        .modified(Collections.emptyList())
                        .removed(Collections.emptyList())
                        .build())
                .volumes(ResourceChanges.builder()
                        .added(Collections.emptyList())
                        .modified(Collections.emptyList())
                        .removed(Collections.emptyList())
                        .build())
                .build();

        return CommandExecuteResult.builder()
                .command(command.getOriginalCommand())
                .success(true)
                .console(console)
                .stateChanges(stateChanges)
                .summary(createSummary(simulation))
                .hints(createStopHints(container))
                .executedAt(LocalDateTime.now())
                .resourceId(container.getContainerId())
                .build();
    }

    private CommandExecuteResult createRestartResult(ParsedDockerCommand command, Container container,
            Simulation simulation) {
        ConsoleOutput console = ConsoleOutput.builder()
                .output(Arrays.asList(container.getName()))
                .finalLine(container.getName())
                .success(true)
                .build();

        StateChanges stateChanges = StateChanges.builder()
                .containers(ResourceChanges.builder()
                        .added(Collections.emptyList())
                        .modified(Arrays.asList(container))
                        .removed(Collections.emptyList())
                        .build())
                .images(ResourceChanges.builder()
                        .added(Collections.emptyList())
                        .modified(Collections.emptyList())
                        .removed(Collections.emptyList())
                        .build())
                .networks(ResourceChanges.builder()
                        .added(Collections.emptyList())
                        .modified(Collections.emptyList())
                        .removed(Collections.emptyList())
                        .build())
                .volumes(ResourceChanges.builder()
                        .added(Collections.emptyList())
                        .modified(Collections.emptyList())
                        .removed(Collections.emptyList())
                        .build())
                .build();

        return CommandExecuteResult.builder()
                .command(command.getOriginalCommand())
                .success(true)
                .console(console)
                .stateChanges(stateChanges)
                .summary(createSummary(simulation))
                .hints(createRestartHints(container))
                .executedAt(LocalDateTime.now())
                .resourceId(container.getContainerId())
                .build();
    }

    private CommandExecuteResult createRmResult(ParsedDockerCommand command, Container container,
            Simulation simulation) {
        ConsoleOutput console = ConsoleOutput.builder()
                .output(Arrays.asList(container.getName()))
                .finalLine(container.getName())
                .success(true)
                .build();

        StateChanges stateChanges = StateChanges.builder()
                .containers(ResourceChanges.builder()
                        .added(Collections.emptyList())
                        .modified(Collections.emptyList())
                        .removed(Arrays.asList(container))
                        .build())
                .images(ResourceChanges.builder()
                        .added(Collections.emptyList())
                        .modified(Collections.emptyList())
                        .removed(Collections.emptyList())
                        .build())
                .networks(ResourceChanges.builder()
                        .added(Collections.emptyList())
                        .modified(Collections.emptyList())
                        .removed(Collections.emptyList())
                        .build())
                .volumes(ResourceChanges.builder()
                        .added(Collections.emptyList())
                        .modified(Collections.emptyList())
                        .removed(Collections.emptyList())
                        .build())
                .build();

        return CommandExecuteResult.builder()
                .command(command.getOriginalCommand())
                .success(true)
                .console(console)
                .stateChanges(stateChanges)
                .summary(createSummary(simulation))
                .hints(createRmHints(container))
                .executedAt(LocalDateTime.now())
                .resourceId(container.getContainerId())
                .build();
    }

    private CommandExecuteResult createLogsResult(ParsedDockerCommand command, Container container,
            Simulation simulation) {
        List<String> outputLines = new java.util.ArrayList<>();

        // 시뮬레이션된 nginx 로그
        outputLines.add("2024/01/15 10:30:01 [notice] 1#1: using the \"epoll\" event method");
        outputLines.add("2024/01/15 10:30:01 [notice] 1#1: nginx/1.25.3");
        outputLines.add("2024/01/15 10:30:01 [notice] 1#1: built by gcc 12.2.0 (Debian 12.2.0-14)");
        outputLines.add("2024/01/15 10:30:01 [notice] 1#1: OS: Linux 5.15.0-56-generic");
        outputLines.add("2024/01/15 10:30:01 [notice] 1#1: getrlimit(RLIMIT_NOFILE): 1048576:1048576");
        outputLines.add("2024/01/15 10:30:01 [notice] 1#1: start worker processes");
        outputLines.add("2024/01/15 10:30:01 [notice] 1#1: start worker process 29");
        outputLines.add("127.0.0.1 - - [15/Jan/2024:10:30:15 +0000] \"GET / HTTP/1.1\" 200 615");
        outputLines.add("127.0.0.1 - - [15/Jan/2024:10:30:16 +0000] \"GET /favicon.ico HTTP/1.1\" 404 555");

        ConsoleOutput console = ConsoleOutput.builder()
                .output(outputLines)
                .success(true)
                .build();

        StateChanges stateChanges = createEmptyStateChanges();

        return CommandExecuteResult.builder()
                .command(command.getOriginalCommand())
                .success(true)
                .console(console)
                .stateChanges(stateChanges)
                .summary(createSummary(simulation))
                .hints(createLogsHints(container))
                .executedAt(LocalDateTime.now())
                .resourceId(container.getContainerId())
                .build();
    }

    private CommandExecuteResult createInspectResult(ParsedDockerCommand command, Container container,
            Simulation simulation) {
        List<String> outputLines = new java.util.ArrayList<>();

        // 시뮬레이션된 inspect JSON 출력
        outputLines.add("[");
        outputLines.add("    {");
        outputLines.add("        \"Id\": \"" + container.getContainerId() + "\",");
        outputLines.add("        \"Created\": \"2024-01-15T10:30:00.000000000Z\",");
        outputLines.add("        \"Path\": \"/docker-entrypoint.sh\",");
        outputLines.add("        \"Args\": [\"nginx\", \"-g\", \"daemon off;\"],");
        outputLines.add("        \"State\": {");
        outputLines.add("            \"Status\": \"" + container.getStatus().name().toLowerCase() + "\",");
        outputLines.add("            \"Running\": " + container.isRunning() + ",");
        outputLines.add("            \"Paused\": false,");
        outputLines.add("            \"Restarting\": false,");
        outputLines.add("            \"OOMKilled\": false,");
        outputLines.add("            \"Dead\": false,");
        outputLines.add("            \"Pid\": " + (container.isRunning() ? "12345" : "0") + ",");
        outputLines.add("            \"ExitCode\": 0,");
        outputLines.add("            \"StartedAt\": \"2024-01-15T10:30:01.000000000Z\",");
        outputLines.add("            \"FinishedAt\": \"0001-01-01T00:00:00Z\"");
        outputLines.add("        },");
        outputLines.add("        \"Image\": \"sha256:abcd1234\",");
        outputLines.add("        \"Name\": \"/" + container.getName() + "\",");
        outputLines.add("        \"Config\": {");
        outputLines.add("            \"Image\": \"" + container.getImage() + "\",");
        outputLines.add("            \"ExposedPorts\": {");
        outputLines.add("                \"80/tcp\": {}");
        outputLines.add("            }");
        outputLines.add("        },");
        outputLines.add("        \"NetworkSettings\": {");
        outputLines.add("            \"IPAddress\": \"172.17.0.2\",");
        outputLines.add("            \"Ports\": {");
        for (String portMapping : container.getPortMappings()) {
            outputLines.add("                \"80/tcp\": [{ \"HostIp\": \"0.0.0.0\", \"HostPort\": \""
                    + portMapping.split(":")[0] + "\" }],");
        }
        outputLines.add("            }");
        outputLines.add("        }");
        outputLines.add("    }");
        outputLines.add("]");

        ConsoleOutput console = ConsoleOutput.builder()
                .output(outputLines)
                .success(true)
                .build();

        StateChanges stateChanges = createEmptyStateChanges();

        return CommandExecuteResult.builder()
                .command(command.getOriginalCommand())
                .success(true)
                .console(console)
                .stateChanges(stateChanges)
                .summary(createSummary(simulation))
                .hints(createInspectHints(container))
                .executedAt(LocalDateTime.now())
                .resourceId(container.getContainerId())
                .build();
    }

    // === Helper Methods for Result Creation ===

    private StateSummary createSummary(Simulation simulation) {
        return StateSummary.builder()
                .totalContainers(containerRepository.countBySimulationId(simulation.getId()))
                .runningContainers(containerRepository.countRunningContainersBySimulationId(simulation.getId()))
                .totalImages(imageRepository.countBySimulationId(simulation.getId()))
                .totalNetworks(0L)
                .totalVolumes(0L)
                .build();
    }

    private StateChanges createEmptyStateChanges() {
        return StateChanges.builder()
                .containers(ResourceChanges.builder()
                        .added(Collections.emptyList())
                        .modified(Collections.emptyList())
                        .removed(Collections.emptyList())
                        .build())
                .images(ResourceChanges.builder()
                        .added(Collections.emptyList())
                        .modified(Collections.emptyList())
                        .removed(Collections.emptyList())
                        .build())
                .networks(ResourceChanges.builder()
                        .added(Collections.emptyList())
                        .modified(Collections.emptyList())
                        .removed(Collections.emptyList())
                        .build())
                .volumes(ResourceChanges.builder()
                        .added(Collections.emptyList())
                        .modified(Collections.emptyList())
                        .removed(Collections.emptyList())
                        .build())
                .build();
    }

    // === Hint Creation Methods ===

    private LearningHints createRunHints(Container container) {
        return LearningHints.builder()
                .message("컨테이너가 생성되고 시작되었습니다")
                .nextSuggestions(Arrays.asList(
                        "docker ps",
                        "docker logs " + container.getName()))
                .learningTip("💡 컨테이너 중지: docker stop " + container.getName())
                .build();
    }

    private LearningHints createPsHints() {
        return LearningHints.builder()
                .message("컨테이너 목록을 조회했습니다")
                .nextSuggestions(Arrays.asList(
                        "docker stop <컨테이너명>",
                        "docker logs <컨테이너명>"))
                .learningTip("💡 모든 컨테이너 보기: docker ps -a")
                .build();
    }

    private LearningHints createStartHints(Container container) {
        return LearningHints.builder()
                .message("컨테이너가 시작되었습니다")
                .nextSuggestions(Arrays.asList(
                        "docker ps",
                        "docker logs " + container.getName()))
                .learningTip("💡 컨테이너 중지: docker stop " + container.getName())
                .build();
    }

    private LearningHints createStopHints(Container container) {
        return LearningHints.builder()
                .message("컨테이너가 중지되었습니다")
                .nextSuggestions(Arrays.asList(
                        "docker ps -a",
                        "docker start " + container.getName()))
                .learningTip("💡 컨테이너 제거: docker rm " + container.getName())
                .build();
    }

    private LearningHints createRestartHints(Container container) {
        return LearningHints.builder()
                .message("컨테이너가 재시작되었습니다")
                .nextSuggestions(Arrays.asList(
                        "docker ps",
                        "docker logs " + container.getName()))
                .learningTip("💡 재시작은 컨테이너를 멈추고 다시 시작합니다")
                .build();
    }

    private LearningHints createRmHints(Container container) {
        return LearningHints.builder()
                .message("컨테이너가 제거되었습니다")
                .nextSuggestions(Arrays.asList(
                        "docker ps -a",
                        "docker run " + container.getImage()))
                .learningTip("💡 컨테이너 제거는 되돌릴 수 없습니다")
                .build();
    }

    private LearningHints createLogsHints(Container container) {
        return LearningHints.builder()
                .message("컨테이너 로그를 조회했습니다")
                .nextSuggestions(Arrays.asList(
                        "docker ps -a",
                        "docker stop " + container.getName()))
                .learningTip("💡 로그 확인: docker logs " + container.getName())
                .build();
    }

    private LearningHints createInspectHints(Container container) {
        return LearningHints.builder()
                .message("컨테이너 정보를 조회했습니다")
                .nextSuggestions(Arrays.asList(
                        "docker ps -a",
                        "docker stop " + container.getName()))
                .learningTip("💡 정보 확인: docker inspect " + container.getName())
                .build();
    }
}
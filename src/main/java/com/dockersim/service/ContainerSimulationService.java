package com.dockersim.service;

import com.dockersim.dto.ParsedDockerCommand;
import com.dockersim.dto.response.*;
import com.dockersim.entity.*;
import com.dockersim.entity.enums.ContainerStatus;
import com.dockersim.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ContainerSimulationService {

    private final ContainerSimulationRepository containerRepository;
    private final ImageSimulationService imageService;
    private final ObjectMapper objectMapper;

    public CommandExecuteResponse executeCommand(ParsedDockerCommand command, String simulationId) {
        String subCommand = command.getSubCommand();

        return switch (subCommand) {
            case "run" -> handleContainerRun(command, simulationId);
            case "create" -> handleContainerCreate(command, simulationId);
            case "start" -> handleContainerStart(command, simulationId);
            case "stop" -> handleContainerStop(command, simulationId);
            case "restart" -> handleContainerRestart(command, simulationId);
            case "rm" -> handleContainerRemove(command, simulationId);
            case "ps" -> handleContainerList(command, simulationId);
            case "logs" -> handleContainerLogs(command, simulationId);
            case "exec" -> handleContainerExec(command, simulationId);
            case "inspect" -> handleContainerInspect(command, simulationId);
            case "commit" -> handleContainerCommit(command, simulationId);
            default -> createFailureResponse(command.getCommand(),
                    "지원하지 않는 컨테이너 명령어입니다: " + subCommand);
        };
    }

    private CommandExecuteResponse handleContainerRun(ParsedDockerCommand command, String simulationId) {
        try {
            String imageName = command.getImageName();
            String imageTag = command.getImageTag();
            String containerName = command.getContainerName();

            // 컨테이너 이름 생성 (지정되지 않은 경우)
            if (containerName == null) {
                containerName = generateContainerName(imageName);
            }

            // 컨테이너 이름 중복 확인
            if (containerRepository.existsBySimulationIdAndName(simulationId, containerName)) {
                return createFailureResponse(command.getCommand(),
                        "이미 존재하는 컨테이너 이름입니다: " + containerName);
            }

            // 이미지 존재 확인 및 생성
            imageService.ensureImageExists(simulationId, imageName, imageTag);

            // 컨테이너 생성
            ContainerSimulation container = ContainerSimulation.builder()
                    .simulationId(simulationId)
                    .name(containerName)
                    .imageName(imageName)
                    .imageTag(imageTag)
                    .status(command.hasFlag("-d") ? ContainerStatus.RUNNING : ContainerStatus.EXITED)
                    .ports(extractPorts(command))
                    .environment(extractEnvironment(command))
                    .volumes(extractVolumes(command))
                    .networks(extractNetworks(command))
                    .command(extractCommand(command))
                    .workingDir(command.getOption("--workdir"))
                    .startedAt(LocalDateTime.now())
                    .build();

            containerRepository.save(container);

            String output = command.hasFlag("-d")
                    ? "컨테이너 '" + containerName + "'를 백그라운드에서 시작했습니다.\n" + container.getId()
                    : "컨테이너 '" + containerName + "'를 실행했습니다.";

            Map<String, Object> stateChanges = Map.of(
                    "created", List.of(ContainerSimulationDto.from(container)),
                    "action", "container_created");

            return CommandExecuteResponse.builder()
                    .output(output)
                    .success(true)
                    .stateChanges(stateChanges)
                    .hint("컨테이너가 성공적으로 생성되었습니다. 'docker ps'로 실행 중인 컨테이너를 확인할 수 있습니다.")
                    .build();

        } catch (Exception e) {
            log.error("컨테이너 실행 중 오류: {}", e.getMessage(), e);
            return createFailureResponse(command.getCommand(),
                    "컨테이너 실행 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private CommandExecuteResponse handleContainerList(ParsedDockerCommand command, String simulationId) {
        List<ContainerSimulation> containers;

        if (command.hasFlag("-a")) {
            containers = containerRepository.findBySimulationId(simulationId);
        } else {
            containers = containerRepository.findBySimulationIdAndStatus(simulationId, ContainerStatus.RUNNING);
        }

        StringBuilder output = new StringBuilder();
        output.append(
                "CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS               NAMES\n");

        for (ContainerSimulation container : containers) {
            output.append(String.format("%-20s %-20s %-20s %-20s %-20s %-20s %-20s\n",
                    container.getId().toString().substring(0, Math.min(12, container.getId().toString().length())),
                    container.getImageName() + ":" + container.getImageTag(),
                    container.getCommand() != null ? container.getCommand() : "\"\"",
                    "About a minute ago",
                    container.getStatus().name().toLowerCase(),
                    extractPortsDisplay(container.getPorts()),
                    container.getName()));
        }

        return CommandExecuteResponse.builder()
                .output(output.toString())
                .success(true)
                .hint("컨테이너 목록을 표시했습니다. '-a' 플래그로 중지된 컨테이너도 확인할 수 있습니다.")
                .build();
    }

    private CommandExecuteResponse handleContainerStop(ParsedDockerCommand command, String simulationId) {
        String containerName = getContainerNameFromCommand(command);
        Optional<ContainerSimulation> containerOpt = containerRepository.findBySimulationIdAndName(simulationId,
                containerName);

        if (!containerOpt.isPresent()) {
            return createFailureResponse(command.getCommand(), "컨테이너를 찾을 수 없습니다: " + containerName);
        }

        ContainerSimulation container = containerOpt.get();
        container.setStatus(ContainerStatus.STOPPED);
        container.setStoppedAt(LocalDateTime.now());
        containerRepository.save(container);

        return CommandExecuteResponse.builder()
                .output("컨테이너 '" + containerName + "'를 중지했습니다.")
                .success(true)
                .stateChanges(Map.of("stopped", List.of(ContainerSimulationDto.from(container))))
                .build();
    }

    private CommandExecuteResponse handleContainerStart(ParsedDockerCommand command, String simulationId) {
        String containerName = getContainerNameFromCommand(command);
        Optional<ContainerSimulation> containerOpt = containerRepository.findBySimulationIdAndName(simulationId,
                containerName);

        if (!containerOpt.isPresent()) {
            return createFailureResponse(command.getCommand(), "컨테이너를 찾을 수 없습니다: " + containerName);
        }

        ContainerSimulation container = containerOpt.get();
        container.setStatus(ContainerStatus.RUNNING);
        container.setStartedAt(LocalDateTime.now());
        containerRepository.save(container);

        return CommandExecuteResponse.builder()
                .output("컨테이너 '" + containerName + "'를 시작했습니다.")
                .success(true)
                .stateChanges(Map.of("started", List.of(ContainerSimulationDto.from(container))))
                .build();
    }

    private CommandExecuteResponse handleContainerRestart(ParsedDockerCommand command, String simulationId) {
        CommandExecuteResponse stopResult = handleContainerStop(command, simulationId);
        if (!stopResult.getSuccess()) {
            return stopResult;
        }

        CommandExecuteResponse startResult = handleContainerStart(command, simulationId);
        if (!startResult.getSuccess()) {
            return startResult;
        }

        String containerName = getContainerNameFromCommand(command);
        return CommandExecuteResponse.builder()
                .output("컨테이너 '" + containerName + "'를 재시작했습니다.")
                .success(true)
                .build();
    }

    private CommandExecuteResponse handleContainerRemove(ParsedDockerCommand command, String simulationId) {
        String containerName = getContainerNameFromCommand(command);
        Optional<ContainerSimulation> containerOpt = containerRepository.findBySimulationIdAndName(simulationId,
                containerName);

        if (!containerOpt.isPresent()) {
            return createFailureResponse(command.getCommand(), "컨테이너를 찾을 수 없습니다: " + containerName);
        }

        ContainerSimulation container = containerOpt.get();

        if (container.getStatus() == ContainerStatus.RUNNING && !command.hasFlag("-f")) {
            return createFailureResponse(command.getCommand(),
                    "실행 중인 컨테이너는 삭제할 수 없습니다. 먼저 중지하거나 '-f' 옵션을 사용하세요.");
        }

        containerRepository.delete(container);

        return CommandExecuteResponse.builder()
                .output("컨테이너 '" + containerName + "'를 삭제했습니다.")
                .success(true)
                .stateChanges(Map.of("removed", List.of(ContainerSimulationDto.from(container))))
                .build();
    }

    private CommandExecuteResponse handleContainerLogs(ParsedDockerCommand command, String simulationId) {
        String containerName = getContainerNameFromCommand(command);
        return CommandExecuteResponse.builder()
                .output("컨테이너 '" + containerName + "'의 로그를 표시합니다.\n[시뮬레이션] 로그 내용...")
                .success(true)
                .hint("실제 환경에서는 컨테이너의 실시간 로그가 표시됩니다.")
                .build();
    }

    private CommandExecuteResponse handleContainerExec(ParsedDockerCommand command, String simulationId) {
        String containerName = command.getArguments().get(0);
        String execCommand = String.join(" ", command.getArguments().subList(1, command.getArguments().size()));

        return CommandExecuteResponse.builder()
                .output("컨테이너 '" + containerName + "'에서 명령어 '" + execCommand + "'를 실행했습니다.\n[시뮬레이션] 명령어 실행 결과...")
                .success(true)
                .hint("실제 환경에서는 컨테이너 내부에서 명령어가 실행됩니다.")
                .build();
    }

    /**
     * docker create 처리 - 컨테이너 생성만 하고 시작하지 않음
     */
    private CommandExecuteResponse handleContainerCreate(ParsedDockerCommand command, String simulationId) {
        try {
            String imageName = command.getImageName();
            String imageTag = command.getImageTag();
            String containerName = command.getContainerName();

            // 컨테이너 이름 생성 (지정되지 않은 경우)
            if (containerName == null) {
                containerName = generateContainerName(imageName);
            }

            // 컨테이너 이름 중복 확인
            if (containerRepository.existsBySimulationIdAndName(simulationId, containerName)) {
                return createFailureResponse(command.getCommand(),
                        "이미 존재하는 컨테이너 이름입니다: " + containerName);
            }

            // 이미지 존재 확인 및 생성
            imageService.ensureImageExists(simulationId, imageName, imageTag);

            // 컨테이너 생성 (시작하지 않음)
            ContainerSimulation container = ContainerSimulation.builder()
                    .simulationId(simulationId)
                    .name(containerName)
                    .imageName(imageName)
                    .imageTag(imageTag)
                    .status(ContainerStatus.CREATED)
                    .ports(extractPorts(command))
                    .environment(extractEnvironment(command))
                    .volumes(extractVolumes(command))
                    .networks(extractNetworks(command))
                    .command(extractCommand(command))
                    .workingDir(command.getOption("--workdir"))
                    .build();

            containerRepository.save(container);

            return CommandExecuteResponse.builder()
                    .output(container.getId().toString())
                    .success(true)
                    .stateChanges(Map.of("created", List.of(ContainerSimulationDto.from(container))))
                    .hint("컨테이너가 생성되었습니다. 'docker start " + containerName + "'로 시작할 수 있습니다.")
                    .build();

        } catch (Exception e) {
            log.error("컨테이너 생성 중 오류 발생: {}", e.getMessage(), e);
            return createFailureResponse(command.getCommand(), "컨테이너 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * docker inspect 처리 - 컨테이너 상세 정보 조회
     */
    private CommandExecuteResponse handleContainerInspect(ParsedDockerCommand command, String simulationId) {
        String containerName = getContainerNameFromCommand(command);

        if (containerName == null) {
            return createFailureResponse(command.getCommand(), "컨테이너 이름 또는 ID가 필요합니다.");
        }

        Optional<ContainerSimulation> containerOpt = containerRepository.findBySimulationIdAndName(simulationId,
                containerName);

        if (!containerOpt.isPresent()) {
            return createFailureResponse(command.getCommand(), "컨테이너를 찾을 수 없습니다: " + containerName);
        }

        ContainerSimulation container = containerOpt.get();
        Map<String, Object> inspectData = createContainerInspectData(container);

        try {
            String jsonOutput = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(List.of(inspectData));
            return CommandExecuteResponse.builder()
                    .output(jsonOutput)
                    .success(true)
                    .hint("컨테이너의 상세 정보를 JSON 형태로 표시했습니다.")
                    .build();
        } catch (JsonProcessingException e) {
            return createFailureResponse(command.getCommand(), "컨테이너 정보 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * docker commit 처리 - 컨테이너를 이미지로 변환
     */
    private CommandExecuteResponse handleContainerCommit(ParsedDockerCommand command, String simulationId) {
        List<String> args = command.getArguments();

        if (args.isEmpty()) {
            return createFailureResponse(command.getCommand(),
                    "컨테이너 이름이 필요합니다. 예: docker commit my-container my-image:v1.0");
        }

        String containerName = args.get(0);
        String newImageName = args.size() > 1 ? args.get(1) : null;

        // 컨테이너 존재 확인
        Optional<ContainerSimulation> containerOpt = containerRepository.findBySimulationIdAndName(simulationId,
                containerName);

        if (!containerOpt.isPresent()) {
            return createFailureResponse(command.getCommand(), "컨테이너를 찾을 수 없습니다: " + containerName);
        }

        ContainerSimulation container = containerOpt.get();

        // 새 이미지 이름 생성 (지정되지 않은 경우)
        if (newImageName == null) {
            newImageName = container.getImageName() + "_commit_" + System.currentTimeMillis();
        }

        // 컨테이너를 이미지로 커밋
        try {
            String[] nameParts = parseImageNameAndTag(newImageName);
            String imageName = nameParts[0];
            String tag = nameParts[1];

            // 컨테이너로부터 이미지 생성 (간소화)
            imageService.ensureImageExists(simulationId, imageName, tag);

            return CommandExecuteResponse.builder()
                    .output("sha256:" + generateImageId())
                    .success(true)
                    .hint("컨테이너가 성공적으로 이미지로 커밋되었습니다. 'docker images'로 확인할 수 있습니다.")
                    .build();

        } catch (Exception e) {
            log.error("컨테이너 커밋 중 오류 발생: {}", e.getMessage(), e);
            return createFailureResponse(command.getCommand(), "컨테이너 커밋 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    public List<ContainerSimulationDto> getCurrentContainers(String simulationId) {
        return containerRepository.findBySimulationId(simulationId)
                .stream()
                .map(ContainerSimulationDto::from)
                .toList();
    }

    // 유틸리티 메서드들
    private String generateContainerName(String imageName) {
        return imageName.replaceAll("[^a-zA-Z0-9]", "") + "_" + System.currentTimeMillis();
    }

    private String getContainerNameFromCommand(ParsedDockerCommand command) {
        if (command.getContainerName() != null) {
            return command.getContainerName();
        }
        if (!command.getArguments().isEmpty()) {
            return command.getArguments().get(0);
        }
        return null;
    }

    private String extractPorts(ParsedDockerCommand command) {
        List<String> ports = command.getMultiOption("-p");
        if (ports != null && !ports.isEmpty()) {
            try {
                return objectMapper.writeValueAsString(ports);
            } catch (JsonProcessingException e) {
                return "[]";
            }
        }
        return "[]";
    }

    private String extractEnvironment(ParsedDockerCommand command) {
        List<String> envs = command.getMultiOption("-e");
        if (envs != null && !envs.isEmpty()) {
            try {
                return objectMapper.writeValueAsString(envs);
            } catch (JsonProcessingException e) {
                return "{}";
            }
        }
        return "{}";
    }

    private String extractVolumes(ParsedDockerCommand command) {
        List<String> volumes = command.getMultiOption("-v");
        if (volumes != null && !volumes.isEmpty()) {
            try {
                return objectMapper.writeValueAsString(volumes);
            } catch (JsonProcessingException e) {
                return "[]";
            }
        }
        return "[]";
    }

    private String extractNetworks(ParsedDockerCommand command) {
        String network = command.getOption("--network");
        if (network != null) {
            try {
                return objectMapper.writeValueAsString(List.of(network));
            } catch (JsonProcessingException e) {
                return "[]";
            }
        }
        return "[]";
    }

    private String extractCommand(ParsedDockerCommand command) {
        List<String> args = command.getArguments();
        if (args.size() > 1) {
            return String.join(" ", args.subList(1, args.size()));
        }
        return "/bin/bash";
    }

    private String extractPortsDisplay(String portsJson) {
        try {
            if (portsJson == null || portsJson.equals("[]")) {
                return "";
            }
            List<?> ports = objectMapper.readValue(portsJson, List.class);
            return String.join(", ", ports.stream().map(Object::toString).toArray(String[]::new));
        } catch (Exception e) {
            return "";
        }
    }

    private CommandExecuteResponse createFailureResponse(String command, String errorMessage) {
        return CommandExecuteResponse.builder()
                .command(command)
                .output("Error: " + errorMessage)
                .success(false)
                .build();
    }

    /**
     * 컨테이너 inspect 데이터 생성
     */
    private Map<String, Object> createContainerInspectData(ContainerSimulation container) {
        Map<String, Object> data = new HashMap<>();

        data.put("Id", container.getId().toString());
        data.put("Created", container.getCreatedAt().toString());
        data.put("Path", "/bin/bash");
        data.put("Args", List.of());

        // State 정보
        Map<String, Object> state = new HashMap<>();
        state.put("Status", container.getStatus().name().toLowerCase());
        state.put("Running", container.getStatus() == ContainerStatus.RUNNING);
        state.put("Paused", false);
        state.put("Restarting", false);
        state.put("OOMKilled", false);
        state.put("Dead", false);
        state.put("Pid", container.getStatus() == ContainerStatus.RUNNING ? 1234 : 0);
        state.put("ExitCode", 0);
        state.put("StartedAt", container.getStartedAt() != null ? container.getStartedAt().toString() : "");
        state.put("FinishedAt", container.getStoppedAt() != null ? container.getStoppedAt().toString() : "");
        data.put("State", state);

        // Image 정보
        data.put("Image", container.getImageName() + ":" + container.getImageTag());
        data.put("ResolvConfPath", "/var/lib/docker/containers/" + container.getId() + "/resolv.conf");
        data.put("HostnamePath", "/var/lib/docker/containers/" + container.getId() + "/hostname");
        data.put("HostsPath", "/var/lib/docker/containers/" + container.getId() + "/hosts");
        data.put("LogPath", "/var/lib/docker/containers/" + container.getId() + "/" + container.getId() + "-json.log");

        data.put("Name", "/" + container.getName());
        data.put("RestartCount", 0);
        data.put("Driver", "overlay2");
        data.put("Platform", "linux");
        data.put("MountLabel", "");
        data.put("ProcessLabel", "");
        data.put("AppArmorProfile", "");

        // Config 정보
        Map<String, Object> config = new HashMap<>();
        config.put("Hostname", container.getName());
        config.put("Domainname", "");
        config.put("User", "");
        config.put("AttachStdin", false);
        config.put("AttachStdout", true);
        config.put("AttachStderr", true);
        config.put("Tty", false);
        config.put("OpenStdin", false);
        config.put("StdinOnce", false);

        try {
            List<?> env = objectMapper.readValue(container.getEnvironment(), List.class);
            config.put("Env", env);
        } catch (Exception e) {
            config.put("Env", List.of());
        }

        config.put("Cmd", List.of("/bin/bash"));
        config.put("Image", container.getImageName() + ":" + container.getImageTag());
        config.put("WorkingDir", container.getWorkingDir() != null ? container.getWorkingDir() : "");
        config.put("Entrypoint", List.of());
        config.put("Labels", Map.of());

        data.put("Config", config);

        // NetworkSettings 정보
        Map<String, Object> networkSettings = new HashMap<>();
        networkSettings.put("Bridge", "");
        networkSettings.put("SandboxID", "");
        networkSettings.put("HairpinMode", false);
        networkSettings.put("LinkLocalIPv6Address", "");
        networkSettings.put("LinkLocalIPv6PrefixLen", 0);
        networkSettings.put("Ports", Map.of());
        networkSettings.put("SandboxKey", "");
        networkSettings.put("SecondaryIPAddresses", List.of());
        networkSettings.put("SecondaryIPv6Addresses", List.of());
        networkSettings.put("EndpointID", "");
        networkSettings.put("Gateway", "172.17.0.1");
        networkSettings.put("GlobalIPv6Address", "");
        networkSettings.put("GlobalIPv6PrefixLen", 0);
        networkSettings.put("IPAddress", "172.17.0.2");
        networkSettings.put("IPPrefixLen", 16);
        networkSettings.put("IPv6Gateway", "");
        networkSettings.put("MacAddress", "02:42:ac:11:00:02");
        networkSettings.put("Networks", Map.of());

        data.put("NetworkSettings", networkSettings);

        return data;
    }

    /**
     * 이미지 이름과 태그 파싱
     */
    private String[] parseImageNameAndTag(String fullImageName) {
        String imageName;
        String tag = "latest";

        if (fullImageName.contains(":")) {
            String[] parts = fullImageName.split(":", 2);
            imageName = parts[0];
            tag = parts[1];
        } else {
            imageName = fullImageName;
        }

        return new String[] { imageName, tag };
    }

    /**
     * 이미지 ID 생성 (시뮬레이션용)
     */
    private String generateImageId() {
        return String.format("%064x", System.currentTimeMillis() + (long) (Math.random() * 1000000));
    }
}
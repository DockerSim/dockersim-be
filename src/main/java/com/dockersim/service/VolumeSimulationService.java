package com.dockersim.service;

import com.dockersim.dto.ParsedDockerCommand;
import com.dockersim.dto.response.*;
import com.dockersim.entity.*;
import com.dockersim.entity.enums.MountType;
import com.dockersim.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Docker 볼륨 관련 명령어 시뮬레이션 서비스
 * 
 * 이 클래스는 Docker 볼륨 관리 명령어들을 교육 목적으로 시뮬레이션합니다.
 * 실제 Docker 엔진과 동일한 동작을 모방하여 학습자가 Docker 볼륨 개념을 이해할 수 있도록 돕습니다.
 * 
 * 지원하는 명령어 및 기능:
 * - docker volume create: 새로운 볼륨 생성 (--driver, --name 옵션 지원)
 * - docker volume ls: 볼륨 목록 조회 (필터링 지원)
 * - docker volume inspect: 볼륨 상세 정보 조회 (JSON 형태)
 * - docker volume rm: 볼륨 삭제 (사용 중인 볼륨 검증)
 * - docker volume prune: 사용하지 않는 볼륨 정리 (-f 옵션 지원)
 * 
 * 교육적 특징:
 * - 실제 Docker와 유사한 출력 형태 제공
 * - 볼륨 마운트 및 컨테이너 연결 시뮬레이션
 * - 볼륨 타입별 처리 (volume, bind, tmpfs)
 * - 오류 상황에 대한 명확한 가이드 제공
 * 
 * 제한사항:
 * - 실제 파일 시스템 변경 없음 (시뮬레이션만 제공)
 * - 실제 데이터 저장 불가 (메타데이터로만 관리)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VolumeSimulationService {

    private final VolumeSimulationRepository volumeRepository;
    private final ContainerSimulationRepository containerRepository;
    private final ObjectMapper objectMapper;

    // 상수 정의
    private static final String DEFAULT_DRIVER = "local";
    private static final String ERROR_VOLUME_NOT_FOUND = "볼륨을 찾을 수 없습니다: %s";
    private static final String ERROR_VOLUME_EXISTS = "이미 존재하는 볼륨입니다: %s";
    private static final String ERROR_VOLUME_IN_USE = "볼륨이 사용 중입니다: %s";
    private static final String ERROR_MISSING_ARGUMENTS = "볼륨 이름이 필요합니다.";

    public CommandExecuteResponse executeCommand(ParsedDockerCommand command, String simulationId) {
        String subCommand = command.getSubCommand();

        return switch (subCommand) {
            case "create" -> handleVolumeCreate(command, simulationId);
            case "ls" -> handleVolumeList(command, simulationId);
            case "inspect" -> handleVolumeInspect(command, simulationId);
            case "rm" -> handleVolumeRemove(command, simulationId);
            case "prune" -> handleVolumePrune(command, simulationId);
            default -> createErrorResponse(String.format("지원하지 않는 볼륨 명령어입니다: %s", subCommand), null);
        };
    }

    /**
     * docker volume create 처리
     */
    private CommandExecuteResponse handleVolumeCreate(ParsedDockerCommand command, String simulationId) {
        String volumeName = extractVolumeName(command);

        if (volumeName == null) {
            volumeName = generateVolumeName();
        }

        // 볼륨 중복 확인
        if (volumeRepository.existsBySimulationIdAndName(simulationId, volumeName)) {
            return createErrorResponse(String.format(ERROR_VOLUME_EXISTS, volumeName),
                    "다른 이름으로 볼륨을 생성하거나 기존 볼륨을 사용하세요.");
        }

        // 볼륨 생성
        VolumeSimulation volume = createVolumeEntity(command, simulationId, volumeName);
        volumeRepository.save(volume);

        log.info("볼륨 생성됨: {} (simulationId: {})", volumeName, simulationId);

        return CommandExecuteResponse.builder()
                .output(volumeName)
                .success(true)
                .stateChanges(Map.of("created", List.of(VolumeSimulationDto.from(volume))))
                .hint("볼륨이 성공적으로 생성되었습니다. 'docker volume ls'로 확인할 수 있습니다.")
                .build();
    }

    /**
     * docker volume ls 처리
     */
    private CommandExecuteResponse handleVolumeList(ParsedDockerCommand command, String simulationId) {
        List<VolumeSimulation> volumes = volumeRepository.findBySimulationId(simulationId);

        StringBuilder output = new StringBuilder();
        output.append("DRIVER    VOLUME NAME\n");

        for (VolumeSimulation volume : volumes) {
            output.append(String.format("%-10s %s\n", volume.getDriver(), volume.getName()));
        }

        if (volumes.isEmpty()) {
            output.append("볼륨이 없습니다.\n");
        }

        return CommandExecuteResponse.builder()
                .output(output.toString())
                .success(true)
                .hint("볼륨 목록을 표시했습니다. 'docker volume create <name>'으로 새 볼륨을 생성할 수 있습니다.")
                .build();
    }

    /**
     * docker volume inspect 처리
     */
    private CommandExecuteResponse handleVolumeInspect(ParsedDockerCommand command, String simulationId) {
        String volumeName = extractVolumeName(command);

        if (volumeName == null) {
            return createErrorResponse(ERROR_MISSING_ARGUMENTS,
                    "예: docker volume inspect my-volume");
        }

        Optional<VolumeSimulation> volumeOpt = volumeRepository.findBySimulationIdAndName(simulationId, volumeName);

        if (!volumeOpt.isPresent()) {
            return createErrorResponse(String.format(ERROR_VOLUME_NOT_FOUND, volumeName),
                    "'docker volume ls'로 사용 가능한 볼륨을 확인하세요.");
        }

        VolumeSimulation volume = volumeOpt.get();
        Map<String, Object> inspectData = createVolumeInspectData(volume);

        try {
            String jsonOutput = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(List.of(inspectData));
            return CommandExecuteResponse.builder()
                    .output(jsonOutput)
                    .success(true)
                    .hint("볼륨의 상세 정보를 JSON 형태로 표시했습니다.")
                    .build();
        } catch (JsonProcessingException e) {
            return createErrorResponse("볼륨 정보 조회 중 오류가 발생했습니다.", null);
        }
    }

    /**
     * docker volume rm 처리
     */
    private CommandExecuteResponse handleVolumeRemove(ParsedDockerCommand command, String simulationId) {
        List<String> volumeNames = command.getArguments();

        if (volumeNames.isEmpty()) {
            return createErrorResponse(ERROR_MISSING_ARGUMENTS,
                    "예: docker volume rm my-volume");
        }

        List<VolumeSimulationDto> removedVolumes = new ArrayList<>();
        StringBuilder output = new StringBuilder();

        for (String volumeName : volumeNames) {
            Optional<VolumeSimulation> volumeOpt = volumeRepository.findBySimulationIdAndName(simulationId, volumeName);

            if (!volumeOpt.isPresent()) {
                output.append(String.format("Error: %s\n", String.format(ERROR_VOLUME_NOT_FOUND, volumeName)));
                continue;
            }

            VolumeSimulation volume = volumeOpt.get();

            // 사용 중인 볼륨 확인
            if (isVolumeInUse(simulationId, volumeName)) {
                output.append(String.format("Error: %s\n", String.format(ERROR_VOLUME_IN_USE, volumeName)));
                continue;
            }

            removedVolumes.add(VolumeSimulationDto.from(volume));
            volumeRepository.delete(volume);
            output.append(String.format("%s\n", volumeName));
        }

        boolean success = !removedVolumes.isEmpty();
        Map<String, Object> stateChanges = success ? Map.of("removed", removedVolumes) : Map.of();

        return CommandExecuteResponse.builder()
                .output(output.toString())
                .success(success)
                .stateChanges(stateChanges)
                .hint(success ? "볼륨이 삭제되었습니다." : "일부 볼륨을 삭제할 수 없습니다.")
                .build();
    }

    /**
     * docker volume prune 처리
     */
    private CommandExecuteResponse handleVolumePrune(ParsedDockerCommand command, String simulationId) {
        boolean force = command.hasFlag("-f") || command.hasFlag("--force") ||
                command.getOption("-f") != null || command.getOption("--force") != null;

        List<VolumeSimulation> unusedVolumes = findUnusedVolumes(simulationId);

        if (unusedVolumes.isEmpty()) {
            return CommandExecuteResponse.builder()
                    .output("Total reclaimed space: 0B")
                    .success(true)
                    .hint("정리할 사용하지 않는 볼륨이 없습니다.")
                    .build();
        }

        if (!force) {
            return generatePruneWarning(unusedVolumes);
        }

        return executePrune(unusedVolumes);
    }

    /**
     * 현재 시뮬레이션의 모든 볼륨 조회
     */
    public List<VolumeSimulationDto> getCurrentVolumes(String simulationId) {
        return volumeRepository.findBySimulationId(simulationId)
                .stream()
                .map(VolumeSimulationDto::from)
                .toList();
    }

    // === 헬퍼 메서드들 ===

    private String extractVolumeName(ParsedDockerCommand command) {
        String name = command.getOption("--name");
        if (name != null) {
            return name;
        }

        if (!command.getArguments().isEmpty()) {
            return command.getArguments().get(0);
        }

        return null;
    }

    private String generateVolumeName() {
        return "vol_" + System.currentTimeMillis();
    }

    private VolumeSimulation createVolumeEntity(ParsedDockerCommand command, String simulationId, String volumeName) {
        String driver = command.getOption("--driver");
        if (driver == null) {
            driver = DEFAULT_DRIVER;
        }

        return VolumeSimulation.builder()
                .simulationId(simulationId)
                .name(volumeName)
                .driver(driver)
                .mountType(MountType.VOLUME)
                .options("{}")
                .attachedContainers("[]")
                .build();
    }

    private Map<String, Object> createVolumeInspectData(VolumeSimulation volume) {
        Map<String, Object> data = new HashMap<>();
        data.put("CreatedAt", volume.getCreatedAt().toString());
        data.put("Driver", volume.getDriver());
        data.put("Labels", Map.of());
        data.put("Mountpoint", "/var/lib/docker/volumes/" + volume.getName() + "/_data");
        data.put("Name", volume.getName());
        data.put("Options", Map.of());
        data.put("Scope", "local");
        return data;
    }

    private boolean isVolumeInUse(String simulationId, String volumeName) {
        // 컨테이너에서 해당 볼륨을 사용하는지 확인
        List<ContainerSimulation> containers = containerRepository.findBySimulationId(simulationId);

        for (ContainerSimulation container : containers) {
            try {
                List<?> volumes = objectMapper.readValue(container.getVolumes(), List.class);
                for (Object vol : volumes) {
                    if (vol.toString().contains(volumeName)) {
                        return true;
                    }
                }
            } catch (Exception e) {
                // JSON 파싱 오류 무시
            }
        }

        return false;
    }

    private List<VolumeSimulation> findUnusedVolumes(String simulationId) {
        List<VolumeSimulation> allVolumes = volumeRepository.findBySimulationId(simulationId);
        return allVolumes.stream()
                .filter(volume -> !isVolumeInUse(simulationId, volume.getName()))
                .toList();
    }

    private CommandExecuteResponse generatePruneWarning(List<VolumeSimulation> volumes) {
        StringBuilder warning = new StringBuilder();
        warning.append("WARNING! This will remove all local volumes not used by at least one container.\n");
        warning.append("Are you sure you want to continue? [y/N] ");

        for (VolumeSimulation volume : volumes) {
            warning.append("\n").append(volume.getName());
        }

        return CommandExecuteResponse.builder()
                .output(warning.toString())
                .success(false)
                .hint("실제 환경에서는 'y'를 입력하여 계속하거나 'N'으로 취소할 수 있습니다. '-f' 옵션으로 강제 실행 가능합니다.")
                .build();
    }

    private CommandExecuteResponse executePrune(List<VolumeSimulation> volumes) {
        List<VolumeSimulationDto> removedVolumes = volumes.stream()
                .map(VolumeSimulationDto::from)
                .toList();

        for (VolumeSimulation volume : volumes) {
            volumeRepository.delete(volume);
        }

        return CommandExecuteResponse.builder()
                .output(String.format("Deleted Volumes:\n%s\n\nTotal reclaimed space: %dMB",
                        String.join("\n", volumes.stream().map(VolumeSimulation::getName).toList()),
                        volumes.size() * 100))
                .success(true)
                .stateChanges(Map.of("removed", removedVolumes))
                .hint("사용하지 않는 볼륨이 정리되었습니다.")
                .build();
    }

    private CommandExecuteResponse createErrorResponse(String errorMessage, String hint) {
        return CommandExecuteResponse.builder()
                .output("Error: " + errorMessage)
                .success(false)
                .hint(hint)
                .build();
    }
}
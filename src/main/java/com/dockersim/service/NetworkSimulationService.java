package com.dockersim.service;

import com.dockersim.dto.ParsedDockerCommand;
import com.dockersim.dto.response.*;
import com.dockersim.entity.*;
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
 * Docker 네트워크 관련 명령어 시뮬레이션 서비스
 * 
 * 이 클래스는 Docker 네트워크 관리 명령어들을 교육 목적으로 시뮬레이션합니다.
 * 실제 Docker 엔진과 동일한 동작을 모방하여 학습자가 Docker 네트워크 개념을 이해할 수 있도록 돕습니다.
 * 
 * 지원하는 명령어 및 기능:
 * - docker network create: 새로운 네트워크 생성 (--driver, --subnet, --gateway 옵션 지원)
 * - docker network ls: 네트워크 목록 조회 (필터링 지원)
 * - docker network inspect: 네트워크 상세 정보 조회 (JSON 형태)
 * - docker network rm: 네트워크 삭제 (사용 중인 네트워크 검증)
 * - docker network connect: 컨테이너를 네트워크에 연결
 * - docker network disconnect: 컨테이너를 네트워크에서 분리
 * 
 * 교육적 특징:
 * - 실제 Docker와 유사한 출력 형태 제공
 * - 네트워크 드라이버별 처리 (bridge, host, overlay)
 * - 컨테이너-네트워크 연결 관리 시뮬레이션
 * - 오류 상황에 대한 명확한 가이드 제공
 * 
 * 제한사항:
 * - 실제 네트워크 구성 변경 없음 (시뮬레이션만 제공)
 * - 실제 트래픽 라우팅 불가 (메타데이터로만 관리)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NetworkSimulationService {

    private final NetworkSimulationRepository networkRepository;
    private final ContainerSimulationRepository containerRepository;
    private final ObjectMapper objectMapper;

    // 상수 정의
    private static final String DEFAULT_DRIVER = "bridge";
    private static final String DEFAULT_SUBNET = "172.17.0.0/16";
    private static final String DEFAULT_GATEWAY = "172.17.0.1";
    private static final String ERROR_NETWORK_NOT_FOUND = "네트워크를 찾을 수 없습니다: %s";
    private static final String ERROR_NETWORK_EXISTS = "이미 존재하는 네트워크입니다: %s";
    private static final String ERROR_NETWORK_IN_USE = "네트워크가 사용 중입니다: %s";
    private static final String ERROR_CONTAINER_NOT_FOUND = "컨테이너를 찾을 수 없습니다: %s";
    private static final String ERROR_MISSING_ARGUMENTS = "네트워크 이름이 필요합니다.";

    public CommandExecuteResponse executeCommand(ParsedDockerCommand command, String simulationId) {
        String subCommand = command.getSubCommand();

        return switch (subCommand) {
            case "create" -> handleNetworkCreate(command, simulationId);
            case "ls" -> handleNetworkList(command, simulationId);
            case "inspect" -> handleNetworkInspect(command, simulationId);
            case "rm" -> handleNetworkRemove(command, simulationId);
            case "connect" -> handleNetworkConnect(command, simulationId);
            case "disconnect" -> handleNetworkDisconnect(command, simulationId);
            default -> createErrorResponse(String.format("지원하지 않는 네트워크 명령어입니다: %s", subCommand), null);
        };
    }

    /**
     * docker network create 처리
     */
    private CommandExecuteResponse handleNetworkCreate(ParsedDockerCommand command, String simulationId) {
        String networkName = extractNetworkName(command);

        if (networkName == null) {
            return createErrorResponse(ERROR_MISSING_ARGUMENTS,
                    "예: docker network create my-network");
        }

        // 네트워크 중복 확인
        if (networkRepository.existsBySimulationIdAndName(simulationId, networkName)) {
            return createErrorResponse(String.format(ERROR_NETWORK_EXISTS, networkName),
                    "다른 이름으로 네트워크를 생성하거나 기존 네트워크를 사용하세요.");
        }

        // 네트워크 생성
        NetworkSimulation network = createNetworkEntity(command, simulationId, networkName);
        networkRepository.save(network);

        log.info("네트워크 생성됨: {} (simulationId: {})", networkName, simulationId);

        return CommandExecuteResponse.builder()
                .output(generateNetworkId(network.getId()))
                .success(true)
                .stateChanges(Map.of("created", List.of(NetworkSimulationDto.from(network))))
                .hint("네트워크가 성공적으로 생성되었습니다. 'docker network ls'로 확인할 수 있습니다.")
                .build();
    }

    /**
     * docker network ls 처리
     */
    private CommandExecuteResponse handleNetworkList(ParsedDockerCommand command, String simulationId) {
        List<NetworkSimulation> networks = networkRepository.findBySimulationId(simulationId);

        // 기본 네트워크들도 포함
        if (networks.isEmpty()) {
            networks = createDefaultNetworks(simulationId);
        }

        StringBuilder output = new StringBuilder();
        output.append("NETWORK ID     NAME      DRIVER    SCOPE\n");

        for (NetworkSimulation network : networks) {
            String networkId = generateNetworkId(network.getId());
            output.append(String.format("%-15s %-10s %-10s %s\n",
                    networkId, network.getName(), network.getDriver(), "local"));
        }

        return CommandExecuteResponse.builder()
                .output(output.toString())
                .success(true)
                .hint("네트워크 목록을 표시했습니다. 'docker network create <name>'으로 새 네트워크를 생성할 수 있습니다.")
                .build();
    }

    /**
     * docker network inspect 처리
     */
    private CommandExecuteResponse handleNetworkInspect(ParsedDockerCommand command, String simulationId) {
        String networkName = extractNetworkName(command);

        if (networkName == null) {
            return createErrorResponse(ERROR_MISSING_ARGUMENTS,
                    "예: docker network inspect my-network");
        }

        Optional<NetworkSimulation> networkOpt = networkRepository.findBySimulationIdAndName(simulationId, networkName);

        if (!networkOpt.isPresent()) {
            return createErrorResponse(String.format(ERROR_NETWORK_NOT_FOUND, networkName),
                    "'docker network ls'로 사용 가능한 네트워크를 확인하세요.");
        }

        NetworkSimulation network = networkOpt.get();
        Map<String, Object> inspectData = createNetworkInspectData(network);

        try {
            String jsonOutput = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(List.of(inspectData));
            return CommandExecuteResponse.builder()
                    .output(jsonOutput)
                    .success(true)
                    .hint("네트워크의 상세 정보를 JSON 형태로 표시했습니다.")
                    .build();
        } catch (JsonProcessingException e) {
            return createErrorResponse("네트워크 정보 조회 중 오류가 발생했습니다.", null);
        }
    }

    /**
     * docker network rm 처리
     */
    private CommandExecuteResponse handleNetworkRemove(ParsedDockerCommand command, String simulationId) {
        List<String> networkNames = command.getArguments();

        if (networkNames.isEmpty()) {
            return createErrorResponse(ERROR_MISSING_ARGUMENTS,
                    "예: docker network rm my-network");
        }

        List<NetworkSimulationDto> removedNetworks = new ArrayList<>();
        StringBuilder output = new StringBuilder();

        for (String networkName : networkNames) {
            // 기본 네트워크 삭제 방지
            if (isDefaultNetwork(networkName)) {
                output.append(String.format("Error: %s는 기본 네트워크이므로 삭제할 수 없습니다.\n", networkName));
                continue;
            }

            Optional<NetworkSimulation> networkOpt = networkRepository.findBySimulationIdAndName(simulationId,
                    networkName);

            if (!networkOpt.isPresent()) {
                output.append(String.format("Error: %s\n", String.format(ERROR_NETWORK_NOT_FOUND, networkName)));
                continue;
            }

            NetworkSimulation network = networkOpt.get();

            // 사용 중인 네트워크 확인
            if (isNetworkInUse(simulationId, networkName)) {
                output.append(String.format("Error: %s\n", String.format(ERROR_NETWORK_IN_USE, networkName)));
                continue;
            }

            removedNetworks.add(NetworkSimulationDto.from(network));
            networkRepository.delete(network);
            output.append(String.format("%s\n", networkName));
        }

        boolean success = !removedNetworks.isEmpty();
        Map<String, Object> stateChanges = success ? Map.of("removed", removedNetworks) : Map.of();

        return CommandExecuteResponse.builder()
                .output(output.toString())
                .success(success)
                .stateChanges(stateChanges)
                .hint(success ? "네트워크가 삭제되었습니다." : "일부 네트워크를 삭제할 수 없습니다.")
                .build();
    }

    /**
     * docker network connect 처리
     */
    private CommandExecuteResponse handleNetworkConnect(ParsedDockerCommand command, String simulationId) {
        List<String> args = command.getArguments();

        if (args.size() < 2) {
            return createErrorResponse("네트워크 이름과 컨테이너 이름이 필요합니다.",
                    "예: docker network connect my-network my-container");
        }

        String networkName = args.get(0);
        String containerName = args.get(1);

        // 네트워크 존재 확인
        Optional<NetworkSimulation> networkOpt = networkRepository.findBySimulationIdAndName(simulationId, networkName);
        if (!networkOpt.isPresent()) {
            return createErrorResponse(String.format(ERROR_NETWORK_NOT_FOUND, networkName), null);
        }

        // 컨테이너 존재 확인
        Optional<ContainerSimulation> containerOpt = containerRepository.findBySimulationIdAndName(simulationId,
                containerName);
        if (!containerOpt.isPresent()) {
            return createErrorResponse(String.format(ERROR_CONTAINER_NOT_FOUND, containerName), null);
        }

        // 네트워크에 컨테이너 연결
        connectContainerToNetwork(networkOpt.get(), containerOpt.get());

        return CommandExecuteResponse.builder()
                .output(String.format("컨테이너 '%s'를 네트워크 '%s'에 연결했습니다.", containerName, networkName))
                .success(true)
                .hint("컨테이너가 네트워크에 성공적으로 연결되었습니다.")
                .build();
    }

    /**
     * docker network disconnect 처리
     */
    private CommandExecuteResponse handleNetworkDisconnect(ParsedDockerCommand command, String simulationId) {
        List<String> args = command.getArguments();

        if (args.size() < 2) {
            return createErrorResponse("네트워크 이름과 컨테이너 이름이 필요합니다.",
                    "예: docker network disconnect my-network my-container");
        }

        String networkName = args.get(0);
        String containerName = args.get(1);

        // 네트워크 존재 확인
        Optional<NetworkSimulation> networkOpt = networkRepository.findBySimulationIdAndName(simulationId, networkName);
        if (!networkOpt.isPresent()) {
            return createErrorResponse(String.format(ERROR_NETWORK_NOT_FOUND, networkName), null);
        }

        // 컨테이너 존재 확인
        Optional<ContainerSimulation> containerOpt = containerRepository.findBySimulationIdAndName(simulationId,
                containerName);
        if (!containerOpt.isPresent()) {
            return createErrorResponse(String.format(ERROR_CONTAINER_NOT_FOUND, containerName), null);
        }

        // 네트워크에서 컨테이너 분리
        disconnectContainerFromNetwork(networkOpt.get(), containerOpt.get());

        return CommandExecuteResponse.builder()
                .output(String.format("컨테이너 '%s'를 네트워크 '%s'에서 분리했습니다.", containerName, networkName))
                .success(true)
                .hint("컨테이너가 네트워크에서 성공적으로 분리되었습니다.")
                .build();
    }

    /**
     * 현재 시뮬레이션의 모든 네트워크 조회
     */
    public List<NetworkSimulationDto> getCurrentNetworks(String simulationId) {
        return networkRepository.findBySimulationId(simulationId)
                .stream()
                .map(NetworkSimulationDto::from)
                .toList();
    }

    // === 헬퍼 메서드들 ===

    private String extractNetworkName(ParsedDockerCommand command) {
        if (!command.getArguments().isEmpty()) {
            return command.getArguments().get(0);
        }
        return null;
    }

    private NetworkSimulation createNetworkEntity(ParsedDockerCommand command, String simulationId,
            String networkName) {
        String driver = command.getOption("--driver");
        if (driver == null) {
            driver = DEFAULT_DRIVER;
        }

        String subnet = command.getOption("--subnet");
        if (subnet == null) {
            subnet = generateSubnet();
        }

        String gateway = command.getOption("--gateway");
        if (gateway == null) {
            gateway = generateGateway(subnet);
        }

        return NetworkSimulation.builder()
                .simulationId(simulationId)
                .name(networkName)
                .driver(driver)
                .subnet(subnet)
                .gateway(gateway)
                .options("{}")
                .attachedContainers("[]")
                .build();
    }

    private List<NetworkSimulation> createDefaultNetworks(String simulationId) {
        List<NetworkSimulation> defaults = new ArrayList<>();

        // bridge 네트워크 (기본)
        defaults.add(NetworkSimulation.builder()
                .simulationId(simulationId)
                .name("bridge")
                .driver("bridge")
                .subnet("172.17.0.0/16")
                .gateway("172.17.0.1")
                .options("{}")
                .attachedContainers("[]")
                .build());

        // host 네트워크
        defaults.add(NetworkSimulation.builder()
                .simulationId(simulationId)
                .name("host")
                .driver("host")
                .options("{}")
                .attachedContainers("[]")
                .build());

        // none 네트워크
        defaults.add(NetworkSimulation.builder()
                .simulationId(simulationId)
                .name("none")
                .driver("null")
                .options("{}")
                .attachedContainers("[]")
                .build());

        for (NetworkSimulation network : defaults) {
            if (!networkRepository.existsBySimulationIdAndName(simulationId, network.getName())) {
                networkRepository.save(network);
            }
        }

        return networkRepository.findBySimulationId(simulationId);
    }

    private Map<String, Object> createNetworkInspectData(NetworkSimulation network) {
        Map<String, Object> data = new HashMap<>();
        data.put("Name", network.getName());
        data.put("Id", generateNetworkId(network.getId()));
        data.put("Created", network.getCreatedAt().toString());
        data.put("Scope", "local");
        data.put("Driver", network.getDriver());
        data.put("EnableIPv6", false);

        Map<String, Object> ipam = new HashMap<>();
        ipam.put("Driver", "default");
        if (network.getSubnet() != null) {
            ipam.put("Config", List.of(Map.of("Subnet", network.getSubnet(), "Gateway", network.getGateway())));
        }
        data.put("IPAM", ipam);

        data.put("Internal", false);
        data.put("Attachable", false);
        data.put("Ingress", false);
        data.put("Containers", parseAttachedContainers(network.getAttachedContainers()));
        data.put("Options", Map.of());
        data.put("Labels", Map.of());

        return data;
    }

    private String generateNetworkId(Long id) {
        return String.format("%012x", id != null ? id : System.currentTimeMillis());
    }

    private String generateSubnet() {
        Random random = new Random();
        int second = random.nextInt(255);
        int third = random.nextInt(255);
        return String.format("172.%d.%d.0/24", second, third);
    }

    private String generateGateway(String subnet) {
        if (subnet != null && subnet.contains("/")) {
            String network = subnet.split("/")[0];
            String[] parts = network.split("\\.");
            if (parts.length >= 3) {
                return String.format("%s.%s.%s.1", parts[0], parts[1], parts[2]);
            }
        }
        return "172.17.0.1";
    }

    private boolean isDefaultNetwork(String networkName) {
        return Arrays.asList("bridge", "host", "none").contains(networkName);
    }

    private boolean isNetworkInUse(String simulationId, String networkName) {
        List<ContainerSimulation> containers = containerRepository.findBySimulationId(simulationId);

        for (ContainerSimulation container : containers) {
            try {
                List<?> networks = objectMapper.readValue(container.getNetworks(), List.class);
                for (Object net : networks) {
                    if (net.toString().contains(networkName)) {
                        return true;
                    }
                }
            } catch (Exception e) {
                // JSON 파싱 오류 무시
            }
        }

        return false;
    }

    private void connectContainerToNetwork(NetworkSimulation network, ContainerSimulation container) {
        try {
            // 네트워크의 연결된 컨테이너 목록 업데이트
            List<Map<String, Object>> attachedContainers = parseAttachedContainers(network.getAttachedContainers());

            Map<String, Object> containerInfo = new HashMap<>();
            containerInfo.put("name", container.getName());
            containerInfo.put("id", container.getId().toString());
            containerInfo.put("ipv4Address", generateContainerIP(network.getSubnet()));

            attachedContainers.add(containerInfo);
            network.setAttachedContainers(objectMapper.writeValueAsString(attachedContainers));
            networkRepository.save(network);

            // 컨테이너의 네트워크 정보 업데이트
            updateContainerNetworks(container, network.getName());

        } catch (Exception e) {
            log.error("컨테이너-네트워크 연결 중 오류: {}", e.getMessage());
        }
    }

    private void disconnectContainerFromNetwork(NetworkSimulation network, ContainerSimulation container) {
        try {
            // 네트워크의 연결된 컨테이너 목록에서 제거
            List<Map<String, Object>> attachedContainers = parseAttachedContainers(network.getAttachedContainers());

            attachedContainers.removeIf(containerInfo -> container.getName().equals(containerInfo.get("name")));

            network.setAttachedContainers(objectMapper.writeValueAsString(attachedContainers));
            networkRepository.save(network);

            // 컨테이너의 네트워크 정보에서 제거
            removeContainerNetwork(container, network.getName());

        } catch (Exception e) {
            log.error("컨테이너-네트워크 분리 중 오류: {}", e.getMessage());
        }
    }

    private List<Map<String, Object>> parseAttachedContainers(String attachedContainersJson) {
        try {
            if (attachedContainersJson == null || attachedContainersJson.equals("[]")) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(attachedContainersJson, List.class);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private String generateContainerIP(String subnet) {
        if (subnet == null)
            return "172.17.0.2";

        try {
            String[] parts = subnet.split("/")[0].split("\\.");
            Random random = new Random();
            int lastOctet = 2 + random.nextInt(253); // 2-254 범위
            return String.format("%s.%s.%s.%d", parts[0], parts[1], parts[2], lastOctet);
        } catch (Exception e) {
            return "172.17.0.2";
        }
    }

    private void updateContainerNetworks(ContainerSimulation container, String networkName) {
        try {
            List<String> networks = objectMapper.readValue(container.getNetworks(), List.class);
            if (!networks.contains(networkName)) {
                networks.add(networkName);
                container.setNetworks(objectMapper.writeValueAsString(networks));
                containerRepository.save(container);
            }
        } catch (Exception e) {
            // 기본 네트워크 설정
            container.setNetworks("[\"" + networkName + "\"]");
            containerRepository.save(container);
        }
    }

    private void removeContainerNetwork(ContainerSimulation container, String networkName) {
        try {
            List<String> networks = objectMapper.readValue(container.getNetworks(), List.class);
            networks.remove(networkName);
            container.setNetworks(objectMapper.writeValueAsString(networks));
            containerRepository.save(container);
        } catch (Exception e) {
            // JSON 파싱 오류 무시
        }
    }

    private CommandExecuteResponse createErrorResponse(String errorMessage, String hint) {
        return CommandExecuteResponse.builder()
                .output("Error: " + errorMessage)
                .success(false)
                .hint(hint)
                .build();
    }
}
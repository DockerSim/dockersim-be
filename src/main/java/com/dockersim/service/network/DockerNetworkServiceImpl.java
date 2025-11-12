package com.dockersim.service.network;

import com.dockersim.config.SimulationUserPrincipal;
import com.dockersim.domain.ContainerNetwork;
import com.dockersim.domain.DockerContainer;
import com.dockersim.domain.DockerNetwork;
import com.dockersim.domain.Simulation;
import com.dockersim.dto.response.DockerContainerResponse; // 추가
import com.dockersim.dto.response.DockerContainerSimpleInspect;
import com.dockersim.dto.response.DockerNetworkInspect;
import com.dockersim.dto.response.DockerNetworkResponse;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.DockerImageErrorCode;
import com.dockersim.exception.code.DockerNetworkErrorCode;
import com.dockersim.service.container.DockerContainerFinder;
import com.dockersim.service.simulation.SimulationFinder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair; // 추가
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DockerNetworkServiceImpl implements DockerNetworkService {
    private final DockerNetworkRepository dockerNetworkRepository;
    private final DockerContainerFinder dockerContainerFinder;
    private final DockerNetworkFinder dockerNetworkFinder;

    private final SimulationFinder simulationFinder;

    @Override
    public Pair<DockerNetworkResponse, DockerContainerResponse> connect( // 반환 타입 변경
            SimulationUserPrincipal principal,
            String networkNameOrHexId, String containerNameOrHexId
    ) {
        Simulation simulation = simulationFinder.findById(principal.getSimulationId());
        DockerNetwork network = dockerNetworkFinder.findByNameOrHexId(simulation, networkNameOrHexId);
        DockerContainer container = dockerContainerFinder.findByIdentifier(simulation, containerNameOrHexId);

        network.connect(container);
        DockerNetwork savedNetwork = dockerNetworkRepository.save(network); // 네트워크 저장

        // 컨테이너 엔티티를 다시 로드하여 최신 상태를 반영 (네트워크 연결 정보 포함)
        DockerContainer updatedContainer = dockerContainerFinder.findByIdentifier(simulation, containerNameOrHexId);

        DockerNetworkResponse networkResponse = DockerNetworkResponse.from(savedNetwork, List.of(
                String.format("네트워크 %s에 컨테이너 %s가 연결되었습니다.", networkNameOrHexId, containerNameOrHexId)
        ), true, updatedContainer.getId());

        DockerContainerResponse containerResponse = DockerContainerResponse.from(
                List.of(String.format("컨테이너 %s가 네트워크 %s에 연결되었습니다.", containerNameOrHexId, networkNameOrHexId)),
                updatedContainer
        );

        return Pair.of(networkResponse, containerResponse);
    }

    @Override
    public DockerNetworkResponse create(SimulationUserPrincipal principal,
                                        String networkName
    ) {
        Simulation simulation = simulationFinder.findById(principal.getSimulationId());
        if (dockerNetworkFinder.existsByName(simulation, networkName)) {
            throw new BusinessException(DockerNetworkErrorCode.DUPLICATE_NETWORK_NAME, networkName);
        }

        DockerNetwork network = dockerNetworkRepository.save(DockerNetwork.from(simulation, networkName));

        return DockerNetworkResponse.from(network, List.of(network.getHexId()));
    }

    @Override
    public DockerNetworkResponse disconnect(
            SimulationUserPrincipal principal,
            String networkNameOrHexId, String containerNameOrHexId, boolean force
    ) {
        Simulation simulation = simulationFinder.findById(principal.getSimulationId());
        DockerNetwork network = dockerNetworkFinder.findByNameOrHexId(simulation, networkNameOrHexId);
        DockerContainer container = dockerContainerFinder.findByIdentifier(simulation, containerNameOrHexId);

        network.disconnect(container);
        DockerNetwork save = dockerNetworkRepository.save(network);

        return DockerNetworkResponse.from(save, List.of(
                String.format("네트워크 %s에 컨테이너 %s가 %s 연결 해제 되었습니다.", networkNameOrHexId, containerNameOrHexId,
                        force ? "강제로" : ""
                )
        ), false, container.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public List<String> inspect(SimulationUserPrincipal principal, String networkNameOrHexId) {
        Simulation simulation = simulationFinder.findById(principal.getSimulationId());
        DockerNetwork network = dockerNetworkFinder.findByNameOrHexId(simulation, networkNameOrHexId);

        List<ContainerNetwork> containerNetworks = dockerNetworkFinder.getDockerContainerSimpleInspects(
                simulation.getId(), network.getId());

        List<DockerContainerSimpleInspect> containerSimpleInspects = containerNetworks.stream()
                .map(DockerContainerSimpleInspect::from).toList();

        DockerNetworkInspect inspectData = DockerNetworkInspect.from(network, containerSimpleInspects);

        try {
            return Collections.singletonList(
                    (new ObjectMapper()).writerWithDefaultPrettyPrinter().writeValueAsString(List.of(inspectData)));
        } catch (JsonProcessingException e) {
            throw new BusinessException(DockerImageErrorCode.FAIL_CONVERT_INSPECT);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<String> ls(SimulationUserPrincipal principal, boolean quiet) {
        Simulation simulation = simulationFinder.findById(principal.getSimulationId());

        List<DockerNetwork> networks = dockerNetworkFinder.findAll(simulation);

        Stream<String> headerStream, bodyStream;
        if (quiet) {
            headerStream = Stream.of("NETWORK ID");
            bodyStream = networks.stream().map(DockerNetwork::getShortHexId);
        } else {
            headerStream = String.format("%-25s %s", "NETWORK ID", "NAME").lines();
            bodyStream = networks.stream().map(DockerNetwork::getHeader);
        }
        return Stream.concat(headerStream, bodyStream).toList();
    }

    @Override
    public List<DockerNetworkResponse> prune(SimulationUserPrincipal principal) {
        Simulation simulation = simulationFinder.findById(principal.getSimulationId());

        List<DockerNetwork> unusedNetworks = dockerNetworkFinder.findUnusedNetworks(simulation);

        if (unusedNetworks.isEmpty()) {
            return List.of();
        } else {
            return unusedNetworks.stream().map(n -> DockerNetworkResponse.from(n, List.of(n.getName()))).toList();
        }
    }

    @Override
    public DockerNetworkResponse rm(SimulationUserPrincipal principal, String networkNameOrHexId) {
        Simulation simulation = simulationFinder.findById(principal.getSimulationId());
        DockerNetwork network = dockerNetworkFinder.findByNameOrHexId(simulation, networkNameOrHexId);
        if (!network.getContainerNetworks().isEmpty()) {
            throw new BusinessException(DockerNetworkErrorCode.NETWORK_IN_USE, networkNameOrHexId);
        }
        dockerNetworkRepository.delete(network);
        return DockerNetworkResponse.from(network, List.of(network.getHexId()));
    }
}

package com.dockersim.service.container;

import com.dockersim.config.SimulationUserPrincipal;
import com.dockersim.domain.*;
import com.dockersim.dto.response.ContainerInspectData;
import com.dockersim.dto.response.DockerContainerResponse;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.DockerImageErrorCode;
import com.dockersim.repository.DockerContainerRepository;
import com.dockersim.service.image.DockerImageFinder;
import com.dockersim.service.simulation.SimulationFinder;
import com.dockersim.service.user.UserFinder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class DockerContainerServiceImpl implements DockerContainerService {

    private final SimulationFinder simulationFinder;
    private final DockerImageFinder dockerImageFinder;
    private final DockerContainerFinder dockerContainerFinder;
    private final UserFinder userFinder;

    private final DockerContainerRepository repo;

    @Override
    public DockerContainerResponse create(SimulationUserPrincipal principal, String baseImageNameOrHexId, String name) {
        log.debug("컨테이너 생성 전 검증");
        Simulation simulation = simulationFinder.findById(principal.getSimulationId());
        User user = userFinder.findUserById(principal.getUserId());

        log.debug("로컬에서 base image 탐색");
        DockerImage image = dockerImageFinder.findByIdentifierAndLocation(simulation,
                baseImageNameOrHexId, ImageLocation.LOCAL);

        log.debug("컨테이너 생성");
        DockerContainer container = DockerContainer.from(name, image, simulation);

        return DockerContainerResponse.from(
                List.of("[Create Container]: " + container.getHexId()),
                repo.save(container)
        );
    }

    @Override
    public List<String> inspect(SimulationUserPrincipal principal, String containerNameOrHexId) {
        log.debug("컨테이너 상세 조회 전 검증");
        Simulation simulation = simulationFinder.findByPublicId(principal.getSimulationPublicId());

        log.debug("컨테이너 조회");
        DockerContainer container = dockerContainerFinder.findByIdentifier(simulation, containerNameOrHexId);

        log.debug("컨테이너 상세 정보 재가공");
        ContainerInspectData inspectData = ContainerInspectData.from(container);
        try {
            String json = new ObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(List.of(inspectData));
            return List.of(json);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new BusinessException(DockerImageErrorCode.FAIL_CONVERT_INSPECT);
        }
    }

    @Override
    public List<String> ps(SimulationUserPrincipal principal, boolean all, boolean quiet) {
        Simulation simulation = simulationFinder.findById(principal.getSimulationId());
        List<DockerContainer> containers = dockerContainerFinder.findBySimulation(simulation, all);

        Stream<String> headerStream, bodyStream;
        if (quiet) {
            headerStream = Stream.of("CONTAINER ID");
            bodyStream = containers.stream().map(DockerContainer::getHexId);
        } else {
            headerStream = String.format("%-25s %-20s %-20s %-15s", "CONTAINER ID", "IMAGE", "CREATED", "STATUS").lines();
            bodyStream = containers.stream().map(
                    container ->
                            String.format(
                                    "%-25s %-20s %-20s %-15s",
                                    container.getShortHexId(),
                                    container.getBaseImage().getName(),
                                    container.getCreatedAt(),
                                    container.getStatus()
                            )
            );
        }
        return Stream.concat(headerStream, bodyStream).toList();
    }

    @Override
    public DockerContainerResponse pause(SimulationUserPrincipal principal, String containerNameOrHexId) {
        log.debug("컨테이너 일시 중지 전 검증");
        Simulation simulation = simulationFinder.findById(principal.getSimulationId());

        DockerContainer container = dockerContainerFinder.findBySimulationAndIdentifierAndStatus(simulation, containerNameOrHexId, ContainerStatus.RUNNING);
        container.pause();


        return DockerContainerResponse.from(
                List.of("[Pause Container]: " + container.getShortHexId()),
                repo.save(container)
        );
    }

    @Override
    public List<DockerContainerResponse> restart(SimulationUserPrincipal principal, String containerNameOrHexId) {
        return List.of(stop(principal, containerNameOrHexId), start(principal, containerNameOrHexId));
    }

    @Override
    public DockerContainerResponse rm(SimulationUserPrincipal principal, String containerNameOrHexId) {

        Simulation simulation = simulationFinder.findById(principal.getSimulationId());

        DockerContainer container = dockerContainerFinder.findBySimulationAndIdentifierAndStatus(simulation, containerNameOrHexId, ContainerStatus.EXITED);

        repo.delete(container);
        return DockerContainerResponse.from(
                List.of("[Remove Container]: " + container.getShortHexId()),
                container
        );
    }

    @Override
    public DockerContainerResponse start(SimulationUserPrincipal principal, String containerNameOrHexId) {
        log.debug("컨테이너 시작 전 검증");
        Simulation simulation = simulationFinder.findById(principal.getSimulationId());

        DockerContainer container = dockerContainerFinder.findBySimulationAndIdentifierAndStatus(simulation, containerNameOrHexId, ContainerStatus.EXITED);
        container.start();

        return DockerContainerResponse.from(
                List.of("[Pause Container]: " + container.getShortHexId()),
                repo.save(container)
        );
    }

    @Override
    public DockerContainerResponse stop(SimulationUserPrincipal principal, String containerNameOrHexId) {
        log.debug("컨테이너 중지 전 검증");
        Simulation simulation = simulationFinder.findById(principal.getSimulationId());

        DockerContainer container = dockerContainerFinder.findBySimulationAndIdentifierAndStatus(simulation, containerNameOrHexId, ContainerStatus.RUNNING);
        container.stop();

        return DockerContainerResponse.from(
                List.of("[Stop Container]: " + container.getShortHexId()),
                repo.save(container)
        );
    }

    @Override
    public DockerContainerResponse unpause(SimulationUserPrincipal principal, String containerNameOrHexId) {

        log.debug("컨테이너 일시 중지 해제 전 검증");
        Simulation simulation = simulationFinder.findById(principal.getSimulationId());

        DockerContainer container = dockerContainerFinder.findBySimulationAndIdentifierAndStatus(simulation, containerNameOrHexId, ContainerStatus.PAUSED);
        container.unpause();

        return DockerContainerResponse.from(
                List.of("[Unpause Container]: " + container.getShortHexId()),
                repo.save(container)
        );
    }
}

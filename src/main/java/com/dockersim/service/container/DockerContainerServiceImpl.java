package com.dockersim.service.container;

import com.dockersim.context.SimulationContextHolder;
import com.dockersim.domain.ContainerStatus;
import com.dockersim.dto.request.CreateContainerRequest;
import com.dockersim.dto.response.DockerContainerResponse;
import com.dockersim.repository.DockerContainerRepository;
import com.dockersim.service.simulation.SimulationFinder;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class DockerContainerServiceImpl implements DockerContainerService {

    private final ContainerFinder containerFinder;
    private final SimulationFinder simulationFinder;
    private final DockerContainerRepository repo;

    @Override
    public List<String> commitContainer(String containerIdOrName, String newImageName) {
        return List.of();
    }

    @Override
    public List<String> attach(String nameOrId) {
        String simulationId = SimulationContextHolder.getSimulationId();
        containerFinder.findByNameOrIdAndStatusAndSimulationId(nameOrId, ContainerStatus.RUNNING,
            simulationId);

        return List.of(
            "실행 중인 컨테이너(+" + nameOrId + "의 메인 터미널에 연결되었습니다.",
            "'Ctrl + P/Q'으로 터미널을 종료하지 않고 컨테이너의 메인 터미널을 나오거나,",
            "'Ctrl + C' 또는 exit 입력으로 메인 터미널을 종료합니다.",
            "메인 터미널이 종료되면 컨테이너에서 실행할 명령어가 없으므로 컨테이너도 종료됩니다."
        );
    }

    @Override
    public DockerContainerResponse create(CreateContainerRequest request) {
        return null;
    }

    @Override
    public List<String> copyToFromContainer(String source, String destination) {
        return List.of();
    }

    @Override
    public List<String> diffContainer(String containerIdOrName) {
        return List.of();
    }

    @Override
    public List<String> exportContainer(String containerIdOrName) {
        return List.of();
    }

    @Override
    public List<String> executeInContainer(String containerIdOrName, String command,
        boolean interactive, boolean tty) {
        return List.of();
    }

    @Override
    public List<String> listContainers(boolean all, boolean quiet) {
        return List.of();
    }

    @Override
    public List<String> getContainerPorts(String containerIdOrName) {
        return List.of();
    }

    @Override
    public List<String> pruneContainers() {
        return List.of();
    }

    @Override
    public void renameContainer(String oldNameOrId, String newName) {

    }

    @Override
    public List<String> restartContainers(List<String> containerIdsOrNames) {
        return List.of();
    }

    @Override
    public List<String> removeContainers(List<String> containerIdsOrNames, boolean force) {
        return List.of();
    }

    @Override
    public List<String> startContainers(List<String> containerIdsOrNames) {
        return List.of();
    }

    @Override
    public List<String> stopContainers(List<String> containerIdsOrNames) {
        return List.of();
    }

    @Override
    public List<String> killContainers(List<String> containerIdsOrNames) {
        return List.of();
    }

    @Override
    public List<String> pauseContainers(List<String> containerIdsOrNames) {
        return List.of();
    }

    @Override
    public List<String> unpauseContainers(List<String> containerIdsOrNames) {
        return List.of();
    }

    @Override
    public String inspectContainer(String containerIdOrName) {
        return "";
    }
}

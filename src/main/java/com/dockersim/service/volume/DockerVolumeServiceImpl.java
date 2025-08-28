package com.dockersim.service.volume;

import com.dockersim.config.SimulationUserPrincipal;
import com.dockersim.domain.DockerVolume;
import com.dockersim.domain.Simulation;
import com.dockersim.dto.response.DockerVolumeResponse;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.DockerVolumeErrorCode;
import com.dockersim.repository.DockerVolumeRepository;
import com.dockersim.service.simulation.SimulationFinder;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class DockerVolumeServiceImpl implements DockerVolumeService {

    private final SimulationFinder simulationFinder;
    private final DockerVolumeFinder dockerVolumeFinder;

    private final DockerVolumeRepository repo;

    @Override
    public DockerVolumeResponse create(SimulationUserPrincipal principal, String name,
        boolean anonymous) {
        Simulation simulation = simulationFinder.findById(principal.getSimulationId());

        if (!dockerVolumeFinder.existsByNameAndSimulationId(name, principal.getSimulationId())) {
            throw new BusinessException(DockerVolumeErrorCode.VOLUME_NAME_DUPLICATED, name);
        }

        DockerVolume newVolume = DockerVolume.from(name, anonymous, simulation);
        return DockerVolumeResponse.from(repo.save(newVolume), List.of(
            String.format("CREATE: '%s'", newVolume.getName())
        ));
    }

    @Override
    public List<String> inspect(SimulationUserPrincipal principal, String nameOrHexId) {
        DockerVolume volume = dockerVolumeFinder.findByNameAndSimulationId(nameOrHexId,
            principal.getSimulationId());

        return List.of(
            "[\n",
            "  {\n",
            "    \"CreatedAt\": \"" + volume.getCreateAt() + "\",\n",
            "    \"Driver\": \"local\",\n",
//                "    \"Labels\": {},\n",
            "    \"Mountpoint\": \"/var/lib/docker/volumes/" + volume.getName() + "/_data\",\n",
            "    \"Name\": \"" + volume.getName() + "\",\n",
//                "    \"Options\": {},\n",
//                "    \"Scope\": \"local\"\n",
            "    \"RefCount\": " + volume.getContainerVolumes().size(),
            "  }\n",
            "]\n"
        );
    }

    @Override
    public List<String> ls(SimulationUserPrincipal principal, boolean quiet) {
        List<DockerVolume> volumes = dockerVolumeFinder.findBySimulationId(
            principal.getSimulationId());

        Stream<String> headerStream = Stream.of(
            quiet ? "VOLUME NAME" : String.format("%-20s %s", "DRIVER", "VOLUME NAME"));

        Stream<String> bodyStream = volumes.stream()
            .map(volume -> quiet ? volume.getName()
                : String.format("%-20s %s", "local", volume.getName()));

        return Stream.concat(headerStream, bodyStream).toList();
    }

    @Override
    public List<DockerVolumeResponse> prune(SimulationUserPrincipal principal, boolean all) {
        List<DockerVolume> volumes;

        if (all) {
            volumes = dockerVolumeFinder.findAllUnusedVolumes(principal.getSimulationId());
        } else {
            volumes = dockerVolumeFinder.findUnusedAnonymousVolumes(principal.getSimulationId());
        }

        List<DockerVolumeResponse> responses = volumes.stream()
            .map(volume ->
                DockerVolumeResponse.from(volume, List.of("DELETE: " + volume.getName())))
            .toList();
        repo.deleteAll(volumes);
        return responses;
    }

    @Override
    public DockerVolumeResponse rm(SimulationUserPrincipal principal, String nameOrHexId) {
        DockerVolume volume = dockerVolumeFinder.findUnusedVolumeByNameAndSimulationId(
            nameOrHexId, principal.getSimulationId());
        repo.delete(volume);
        return DockerVolumeResponse.from(volume, List.of("DELETE: " + volume.getName()));
    }
}
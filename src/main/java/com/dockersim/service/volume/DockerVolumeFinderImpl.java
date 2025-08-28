package com.dockersim.service.volume;

import com.dockersim.domain.DockerVolume;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.DockerVolumeErrorCode;
import com.dockersim.repository.DockerVolumeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DockerVolumeFinderImpl implements DockerVolumeFinder {

    private final DockerVolumeRepository repo;

    @Override
    public boolean existsByNameAndSimulationId(String name, Long simulationId) {
        return repo.existsByNameAndSimulationId(name, simulationId);
    }

    @Override
    public DockerVolume findByNameAndSimulationId(String name, Long simulationId) {
        return repo.findByNameAndSimulationId(name, simulationId).orElseThrow(
            () -> new BusinessException(DockerVolumeErrorCode.VOLUME_NAME_DUPLICATED));
    }

    @Override
    public List<DockerVolume> findBySimulationId(Long simulationId) {
        return repo.findBySimulationId(simulationId);
    }

    @Override
    public List<DockerVolume> findUnusedAnonymousVolumes(Long simulationId) {
        return repo.findUnusedAnonymousVolumes(simulationId);
    }

    @Override
    public List<DockerVolume> findAllUnusedVolumes(Long simulationId) {
        return repo.findAllUnusedVolumes(simulationId);
    }

    @Override
    public DockerVolume findUnusedVolumeByNameAndSimulationId(String name, Long simulationId) {
        return repo.findUnusedVolumeByNameAndSimulationId(name, simulationId)
            .orElseThrow(() -> new BusinessException(DockerVolumeErrorCode.VOLUME_NOT_FOUND, name));
    }
}

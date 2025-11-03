package com.dockersim.service.volume;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dockersim.domain.DockerVolume;
import com.dockersim.domain.Simulation;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.DockerVolumeErrorCode;
import com.dockersim.repository.DockerVolumeRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DockerVolumeFinderImpl implements DockerVolumeFinder {

	private final DockerVolumeRepository repo;

	@Override
	public boolean existsBySimulationAndName(Simulation simulation, String name) {
		return repo.existsBySimulationAndName(simulation, name);
	}

	@Override
	public DockerVolume findBySimulationAndName(Simulation simulation, String name) {
		return repo.findBySimulationAndName(simulation, name)
			.orElseThrow(() -> new BusinessException(DockerVolumeErrorCode.VOLUME_NOT_FOUND, name));
	}

	@Override
	public List<DockerVolume> findBySimulation(Simulation simulation) {
		return repo.findBySimulation(simulation);
	}

	@Override
	public List<DockerVolume> findUnusedVolumes(Simulation simulation, boolean all) {
		return repo.findUnusedVolumes(simulation, all);
	}

	@Override
	public DockerVolume findUnusedVolumeBySimulationAndName(Simulation simulation, String name) {
		DockerVolume volume = repo.findBySimulationAndName(simulation, name)
			.orElseThrow(() -> new BusinessException(DockerVolumeErrorCode.VOLUME_NOT_FOUND, name));

		if (volume.getContainerVolumes().isEmpty()) {
			throw new BusinessException(DockerVolumeErrorCode.VOLUME_NAME_DUPLICATED, name);
		}
		return volume;
	}
}

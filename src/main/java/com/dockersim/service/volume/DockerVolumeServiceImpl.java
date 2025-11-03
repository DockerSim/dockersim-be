package com.dockersim.service.volume;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.dockersim.common.IdGenerator;
import com.dockersim.config.SimulationUserPrincipal;
import com.dockersim.domain.DockerVolume;
import com.dockersim.domain.Simulation;
import com.dockersim.dto.response.DockerVolumeResponse;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.DockerVolumeErrorCode;
import com.dockersim.repository.DockerVolumeRepository;
import com.dockersim.service.simulation.SimulationFinder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DockerVolumeServiceImpl implements DockerVolumeService {

	private final SimulationFinder simulationFinder;
	private final DockerVolumeFinder dockerVolumeFinder;

	private final DockerVolumeRepository repo;

	@Override
	public DockerVolumeResponse create(SimulationUserPrincipal principal, String name, boolean anonymous) {
		Simulation simulation = simulationFinder.findById(principal.getSimulationId());

		/*
		name이 존재하지 않는다면 임의의 Hex ID를 이름(name)으로 가집니다.
		 */
		if (name.isEmpty()) {
			name = IdGenerator.generateHexFullId();
		}

		/*
		동일한 이름의 불륨이 이미 있는 경우, 불륨 생성에 실패합니다.
		익명 볼륨의 이름은 새로 생성되는 Hex ID이므로, name은 항상 null이 아닙니다.
		 */
		if (!dockerVolumeFinder.existsBySimulationAndName(simulation, name)) {
			throw new BusinessException(DockerVolumeErrorCode.VOLUME_NAME_DUPLICATED, name);
		}

		DockerVolume newVolume = DockerVolume.from(name, anonymous, simulation);
		return DockerVolumeResponse.from(
			repo.save(newVolume),
			List.of("CREATE: '" + newVolume.getName() + "'")
		);
	}

	@Override
	public List<String> inspect(SimulationUserPrincipal principal, String name) {
		Simulation simulation = simulationFinder.findById(principal.getSimulationId());
		DockerVolume volume = dockerVolumeFinder.findBySimulationAndName(simulation, name);

		return List.of(
			"[\n",
			"  {\n",
			"    \"CreatedAt\": \"" + volume.getCreateAt() + "\",\n",
			"    \"Driver\": \"local\",\n",
			"    \"Mountpoint\": \"" + volume.getMountPoint() + "\",\n",
			"    \"Name\": \"" + volume.getName() + "\",\n",
			"    \"RefCount\": " + volume.getContainerVolumes().size(),
			"  }\n",
			"]\n"
		);
	}

	@Override
	public List<String> ls(SimulationUserPrincipal principal, boolean quiet) {
		Simulation simulation = simulationFinder.findById(principal.getSimulationId());
		List<DockerVolume> volumes = dockerVolumeFinder.findBySimulation(simulation);

		Stream<String> headerStream = Stream.of(
			quiet ? "VOLUME NAME" : String.format("%-20s %s", "DRIVER", "VOLUME NAME"));

		Stream<String> bodyStream = volumes.stream()
			.map(volume -> quiet ? volume.getName()
				: String.format("%-20s %s", "local", volume.getName()));

		return Stream.concat(headerStream, bodyStream).toList();
	}

	@Override
	public List<DockerVolumeResponse> prune(SimulationUserPrincipal principal, boolean all) {
		Simulation simulation = simulationFinder.findById(principal.getSimulationId());
		List<DockerVolume> volumes = dockerVolumeFinder.findUnusedVolumes(simulation, all);
		repo.deleteAll(volumes);
		return volumes.stream()
			.map(volume ->
				DockerVolumeResponse.from(volume, List.of("DELETE: " + volume.getName())))
			.toList();
	}

	@Override
	public DockerVolumeResponse rm(SimulationUserPrincipal principal, String name) {
		Simulation simulation = simulationFinder.findById(principal.getSimulationId());
		DockerVolume volume = dockerVolumeFinder.findUnusedVolumeBySimulationAndName(simulation, name);
		repo.delete(volume);
		return DockerVolumeResponse.from(volume, List.of("DELETE: " + volume.getName()));
	}
}
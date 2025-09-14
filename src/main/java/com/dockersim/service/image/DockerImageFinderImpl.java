package com.dockersim.service.image;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dockersim.domain.DockerImage;
import com.dockersim.domain.ImageLocation;
import com.dockersim.domain.Simulation;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.DockerImageErrorCode;
import com.dockersim.repository.DockerImageRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DockerImageFinderImpl implements DockerImageFinder {

	private final DockerImageRepository repo;

	@Override
	public DockerImage findSameImage(Simulation simulation, String namespace, String name, String tag,
		ImageLocation location) {
		return repo.findBySimulationAndNamespaceAndNameAndTagAndLocation(simulation, namespace, name, tag, location)
			.orElse(null);
	}

	@Override
	public DockerImage findSameImage(Simulation simulation, String hexId, ImageLocation location) {
		return repo.findBySimulationAndHexIdStartsWithAndLocation(simulation, hexId, location).orElseThrow(
			() -> new BusinessException(DockerImageErrorCode.IMAGE_NOT_FOUND, hexId)
		);
	}

	@Override
	public DockerImage findImageByNameOrId(Simulation simulation, Map<String, String> imageInfo, ImageLocation location,
		String hexId) {
		DockerImage image = findSameImage(
			simulation,
			imageInfo.get("namespace"),
			imageInfo.get("repository"),
			imageInfo.get("tag"),
			location
		);
		if (image == null) {
			//  repo[:tag]로 탐색 실패, Hex ID로 간주하고 재탐색
			image = findSameImage(simulation, hexId, ImageLocation.LOCAL);
		}
		return image;
	}

	@Override
	public List<DockerImage> findBySimulationInLocal(Simulation simulation, boolean all) {
		if (all) {
			return repo.findBySimulation(simulation);
		}
		return repo.findNotDanglingImageBySimulation(simulation);
	}

	@Override
	public List<DockerImage> findDanglingImageBySimulationInLocal(Simulation simulation) {
		return repo.findDanglingImageBySimulationInLocal(simulation);
	}

	@Override
	public List<DockerImage> findUnreferencedImageBySimulationInLocal(Simulation simulation) {
		return repo.findUnreferencedImageBySimulationInLocal(simulation);
	}

	@Override
	public List<DockerImage> findPullImageByInfo(Simulation simulation, Map<String, String> info, boolean all) {
		if (all) {
			return repo.findAllBySimulationAndNamespaceAndNameInHub(simulation, info.get("namespace"),
				info.get("repository"));
		}
		return List.of(
			Objects.requireNonNull(
				repo.findBySimulationAndNamespaceAndNameAndTagInHub(simulation, info.get("namespace"),
					info.get("repository"), info.get("tag")).orElse(null))
		);
	}
}

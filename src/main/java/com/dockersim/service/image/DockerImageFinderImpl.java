package com.dockersim.service.image;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dockersim.domain.DockerImage;
import com.dockersim.domain.ImageLocation;
import com.dockersim.domain.Simulation;
import com.dockersim.dto.util.ImageMeta;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.DockerImageErrorCode;
import com.dockersim.repository.DockerImageRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DockerImageFinderImpl implements DockerImageFinder {

	private final DockerImageRepository repo;

	/*
	common
	 */
	@Override
	public DockerImage findImageOrNull(
		Simulation simulation,
		ImageMeta info,
		ImageLocation location
	) {
		return repo.findBySimulationAndNamespaceAndNameAndTagAndLocation(
			simulation, info.getNamespace(), info.getName(), info.getTag(), location
		).orElse(null);
	}

	@Override
	public List<DockerImage> findImages(
		Simulation simulation,
		ImageMeta info,
		ImageLocation location
	) {
		return repo.findBySimulationAndNamespaceAndNameAndLocation(
			simulation, info.getNamespace(), info.getName(), location
		);
	}

	/*
	image push
	 */
	@Override
	public List<DockerImage> findPushImageInLocal(
		Simulation simulation,
		ImageMeta meta,
		boolean allTags
	) {
		List<DockerImage> images;
		if (allTags) {
			images = this.findImages(simulation, meta, ImageLocation.LOCAL);
		} else {
			images = Optional.ofNullable(
				this.findImageOrNull(simulation, meta, ImageLocation.LOCAL)
			).stream().toList();
		}
		if (images.isEmpty()) {
			throw new BusinessException(DockerImageErrorCode.IMAGE_NOT_FOUND_IN_LOCAL, meta.getFullName());
		}
		return images;
	}

	@Override
	public List<DockerImage> findOldPushImageInHub(
		Simulation simulation,
		List<DockerImage> localImages,
		ImageMeta meta,
		boolean allTags
	) {
		List<DockerImage> images;
		if (allTags) {
			images = localImages.stream().map(
				image -> {
					meta.updateTag(image.getTag());
					return this.findImageOrNull(simulation, meta, ImageLocation.HUB);
				}

			).toList();
		} else {
			images = Optional.ofNullable(
				this.findImageOrNull(simulation, meta, ImageLocation.HUB)
			).stream().toList();
		}
		return images;
	}

	// -----------------------------------------------------------------

	public DockerImage findImageInLocalOrNull(Simulation simulation, Map<String, String> info) {
		return this.findImageOrNull(simulation, info, ImageLocation.LOCAL);
	}

	public DockerImage findImageInHubOrNull(Simulation simulation, Map<String, String> info) {
		return this.findImageOrNull(simulation, info, ImageLocation.HUB);
	}

	@Override
	public DockerImage findSameImage(Simulation simulation, String hexId, ImageLocation location) {
		return repo.findBySimulationAndHexIdStartsWithAndLocation(simulation, hexId, location).orElseThrow(
			() -> new BusinessException(DockerImageErrorCode.IMAGE_NOT_FOUND_IN_LOCAL, hexId)
		);
	}

	@Override
	public DockerImage findImageByNameOrId(Simulation simulation, ImageMeta meta, ImageLocation location,
		String hexId) {
		DockerImage image = findImageOrNull(
			simulation,
			meta,
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

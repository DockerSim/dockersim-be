package com.dockersim.service.image;

import java.util.List;
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
	public DockerImage findImageByNameBeforeShortHexId(Simulation simulation, ImageMeta meta, ImageLocation location) {
		return repo.findBySimulationAndShortHexIdAndLocation(simulation, meta.getFullName(), location).orElseGet(
			() -> repo.findBySimulationAndNameAndTagAndLocation(
					simulation,
					meta.getName(),
					meta.getTag(),
					location
				)
				.orElseThrow(
					() -> new BusinessException(DockerImageErrorCode.IMAGE_NOT_FOUND, location, meta.getFullName()))
		);
	}

	@Override
	public DockerImage findImage(Simulation simulation, ImageMeta meta, ImageLocation location) {
		return repo.findBySimulationAndNameAndTagAndLocation(simulation, meta.getName(), meta.getTag(), location)
			.orElseThrow(
				() -> new BusinessException(DockerImageErrorCode.IMAGE_NOT_FOUND, location, meta.getFullName())
			);
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
	image build
	 */

	@Override
	public DockerImage findImageInLocalOrNull(Simulation simulation, ImageMeta meta) {
		return repo.findBySimulationAndNameAndTagAndLocation(simulation, meta.getName(), meta.getTag(),
			ImageLocation.LOCAL).orElse(null);
	}

	/*
	image ls
	 */
	@Override
	public List<DockerImage> findBySimulationInLocal(Simulation simulation, boolean all) {
		if (all) {
			return repo.findBySimulation(simulation);
		}
		return repo.findNotDanglingImageBySimulation(simulation);
	}


	/*
	image pull
	 */

	@Override
	public List<DockerImage> findPullImageByInfo(Simulation simulation, ImageMeta meta, boolean all) {
		if (all) {
			return repo.findAllBySimulationAndNamespaceAndNameInHub(simulation, meta.getNamespace(), meta.getName());
		}
		return List.of(
			Objects.requireNonNull(
				repo.findBySimulationAndNamespaceAndNameAndTagInHub(simulation, meta.getNamespace(), meta.getName(),
					meta.getTag()).orElse(null))
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

	@Override
	public List<DockerImage> findDanglingImageBySimulationInLocal(Simulation simulation) {
		return repo.findDanglingImageBySimulationInLocal(simulation);
	}

	@Override
	public List<DockerImage> findUnreferencedImageBySimulationInLocal(Simulation simulation) {
		return repo.findUnreferencedImageBySimulationInLocal(simulation);
	}
}

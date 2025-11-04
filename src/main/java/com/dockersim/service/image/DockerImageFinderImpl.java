package com.dockersim.service.image;

import com.dockersim.domain.DockerImage;
import com.dockersim.domain.DockerOfficeImage;
import com.dockersim.domain.ImageLocation;
import com.dockersim.domain.Simulation;
import com.dockersim.dto.util.ImageMeta;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.DockerImageErrorCode;
import com.dockersim.repository.DockerImageRepository;
import com.dockersim.repository.DockerOfficeImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DockerImageFinderImpl implements DockerImageFinder {

    private final DockerImageRepository repo;
    private final DockerOfficeImageRepository officeImageRepo;

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
        if (meta.getNamespace().equals("library")) {
            // simulation 없이 조회
            return _findPullImageInOfficeHub(meta, all);
        } else {
            // 사용자 허브에서 조회, simulation 필요
            return _findPullImageInUserHub(simulation, meta, all);
        }
    }


    private List<DockerImage> _findPullImageInOfficeHub(ImageMeta meta, boolean all) {
        if (all) {
            List<DockerOfficeImage> officeImages = officeImageRepo.findAllByName(meta.getName());
            return officeImages.stream().map(image -> DockerImage.from(image, ImageLocation.LOCAL)).toList();
        }
        DockerOfficeImage officeImage = officeImageRepo.findByNameAndTag(meta.getName(), meta.getTag())
                .orElse(null);
        if (officeImage != null) {
            return List.of(DockerImage.from(officeImage, ImageLocation.LOCAL));
        }
        return List.of();
    }

    private List<DockerImage> _findPullImageInUserHub(Simulation simulation, ImageMeta meta, boolean all) {
        if (all) {
            return repo.findAllBySimulationAndNameAndInHub(simulation, meta.getName());
        }
        DockerImage image = repo.findBySimulationAndNameAndTagInHub(simulation, meta.getName(), meta.getTag())
                .orElse(null);

        return List.of(Objects.requireNonNull(image));
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

    @Override
    public DockerImage findByIdentifierAndLocation(
            Simulation simulation,
            String nameOrShortHexId,
            ImageLocation imageLocation
    ) {
        return repo.findByIdentifier(simulation, nameOrShortHexId, imageLocation)
                .orElseThrow(() -> new BusinessException(DockerImageErrorCode.IMAGE_NOT_FOUND, imageLocation, nameOrShortHexId));
    }
}

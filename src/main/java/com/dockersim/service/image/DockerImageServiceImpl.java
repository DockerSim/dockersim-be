package com.dockersim.service.image;

import com.dockersim.common.IdGenerator;
import com.dockersim.context.SimulationContextHolder;
import com.dockersim.domain.ContainerStatus;
import com.dockersim.domain.DockerContainer;
import com.dockersim.domain.DockerImage;
import com.dockersim.domain.ImageLocation;
import com.dockersim.domain.Simulation;
import com.dockersim.dto.response.DockerImageResponse;
import com.dockersim.dto.response.ImageListResponse;
import com.dockersim.dto.response.ImageRemoveResponse;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.DockerImageErrorCode;
import com.dockersim.repository.DockerImageRepository;
import com.dockersim.service.container.ContainerFinder;
import com.dockersim.service.simulation.SimulationFinder;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class DockerImageServiceImpl implements DockerImageService {

    private final DockerImageRepository dockerImageRepository;
    private final SimulationFinder simulationFinder;
    private final ContainerFinder containerFinder;
    private final DockerImageFinder dockerImageFinder;

    @Override
    @Transactional
    public DockerImageResponse pullImage(String name) {
        if (isPotentialHexId(name)) {
            throw new BusinessException(DockerImageErrorCode.PULL_BY_ID_NOT_ALLOWED);
        }

        Map<String, String> parsed = dockerImageFinder.parserImageName(name);
        String repository = parsed.get("repository");
        String tag = parsed.get("tag");
        String namespace;
        String imageName;
        if (repository.contains("/")) {
            String[] parts = repository.split("/", 2);
            namespace = parts[0];
            imageName = parts[1];
        } else {
            // Docker Hub에서 공식 이미지를 가져올 때와 같이 기본 네임스페이스를 'library'로 가정합니다.
            namespace = "library";
            imageName = repository;
        }
        Simulation simulation = getCurrentSimulation();

        DockerImage hubImage = dockerImageRepository.findByNameAndNamespaceAndTagAndLocationAndSimulation(
                imageName, namespace, tag, ImageLocation.HUB, simulation)
            .orElseThrow(
                () -> new BusinessException(DockerImageErrorCode.IMAGE_NOT_FOUND_IN_HUB, name));

        if (dockerImageRepository.existsByNameAndNamespaceAndTagAndLocationAndSimulation(imageName,
            namespace, tag, ImageLocation.LOCAL, simulation)) {
            return DockerImageResponse.from(hubImage, List.of("Image is up to date for " + name));
        }

        DockerImage localImage = DockerImage.from(hubImage, simulation, ImageLocation.LOCAL);

        dockerImageRepository.save(localImage);
        return DockerImageResponse.from(localImage,
            List.of("Status: Downloaded newer image for " + name));
    }

    @Override
    @Transactional
    public DockerImageResponse pushImage(String imageName) {
        if (isPotentialHexId(imageName)) {
            throw new BusinessException(DockerImageErrorCode.PUSH_BY_ID_NOT_ALLOWED);
        }

        Simulation simulation = getCurrentSimulation();
        Map<String, String> parsed = dockerImageFinder.parserImageName(imageName);
        String repository = parsed.get("repository");
        String tag = parsed.get("tag");
        String namespace;
        String name;
        if (repository.contains("/")) {
            String[] parts = repository.split("/", 2);
            namespace = parts[0];
            name = parts[1];
        } else {
            // build와 일관성을 위해 네임스페이스가 없으면 사용자 이름으로 설정합니다.
            namespace = simulation.getOwner().getName();
            name = repository;
        }

        DockerImage localImage = dockerImageRepository.findByNameAndNamespaceAndTagAndLocationAndSimulation(
                name, namespace, tag, ImageLocation.LOCAL, simulation)
            .orElseThrow(
                () -> new BusinessException(DockerImageErrorCode.IMAGE_NOT_FOUND, imageName));

        if (dockerImageRepository.existsByNameAndNamespaceAndTagAndLocationAndSimulation(
            localImage.getName(),
            localImage.getNamespace(), localImage.getTag(), ImageLocation.HUB, simulation)) {
            return DockerImageResponse.from(localImage, List.of("Image already exists in HUB"));
        }

        DockerImage hubImage = DockerImage.from(localImage, simulation, ImageLocation.HUB);

        dockerImageRepository.save(hubImage);
        return DockerImageResponse.from(hubImage, List.of("Image successfully pushed to HUB"));
    }

    @Override
    @Transactional(readOnly = true)
    public ImageListResponse listImages(boolean all, boolean quiet) {
        Simulation simulation = getCurrentSimulation();
        List<DockerImage> localImages = dockerImageRepository.findAllBySimulationAndLocation(
            simulation, ImageLocation.LOCAL);

        List<String> consoleOutput = new ArrayList<>();
        if (quiet) {
            for (DockerImage image : localImages) {
                consoleOutput.add(IdGenerator.getShortId(image.getImageId()));
            }
        } else {
            consoleOutput.add(getConsoleHeader());
            for (DockerImage image : localImages) {
                String repository = image.getNamespace().equals("library") ?
                    image.getName() : image.getNamespace() + "/" + image.getName();
                String shortId = IdGenerator.getShortId(image.getImageId());
                String created = formatDuration(
                    Duration.between(image.getCreatedAt(), LocalDateTime.now()));
                String imageLine = String.format("%-25s %-20s %-15s %-20s", repository,
                    image.getTag(), shortId, created);
                consoleOutput.add(imageLine);
            }
        }
        return ImageListResponse.from(consoleOutput);
    }

    @Override
    @Transactional
    public ImageRemoveResponse removeImage(String imageNameOrId) {
        Simulation simulation = getCurrentSimulation();
        List<DockerImage> imagesToRemove = dockerImageFinder.findImages(imageNameOrId, simulation)
            .stream().filter(i -> i.getLocation() == ImageLocation.LOCAL).toList();

        if (imagesToRemove.isEmpty()) {
            return ImageRemoveResponse.from(List.of("Error: No such image: " + imageNameOrId));
        }
        if (imagesToRemove.size() > 1) {
            return ImageRemoveResponse.from(List.of("Error: \"" + imageNameOrId
                + "\" is ambiguous and matches multiple images. Please specify a tag or a full image ID."));
        }

        DockerImage image = imagesToRemove.get(0);
        if (containerFinder.existsByBaseImageIdAndStatus(image.getImageId(),
            ContainerStatus.RUNNING)) {
            String errorMessage = String.format(
                "Error response from daemon: conflict: unable to remove repository reference \"%s:%s\" (must force) - container is using its referenced image",
                image.getName(), image.getTag());
            return ImageRemoveResponse.from(List.of(errorMessage));
        }

        dockerImageRepository.delete(image);
        return ImageRemoveResponse.from(
            List.of("Untagged: " + image.getFullNameWithTag(), "Deleted: " + image.getImageId()));
    }

    @Override
    @Transactional
    public String buildImage(String name) {
        Simulation simulation = getCurrentSimulation();
        String repository = "<none>";
        String tag = "<none>";
        String namespace = "<none>"; // 사용자의 이름이 네임스페이스 아닌가?
        String imageName = "<none>";

        if (StringUtils.hasText(name)) {
            Map<String, String> parsedTag = dockerImageFinder.parserImageName(name);
            repository = parsedTag.get("repository");
            tag = parsedTag.get("tag");

            if (repository.contains("/")) {
                String[] parts = repository.split("/", 2);
                namespace = parts[0];
                imageName = parts[1];
            } else {
                namespace = simulation.getOwner().getName();
                imageName = repository;
            }

            // 네임스페이스를 포함하여 정확한 기존 이미지를 찾아 Dangling 처리합니다.
            dockerImageRepository.findByNameAndNamespaceAndTagAndLocationAndSimulation(imageName,
                    namespace, tag,
                    ImageLocation.LOCAL, simulation)
                .ifPresent(oldImage -> {
                    oldImage.convertToDangling();
                    dockerImageRepository.save(oldImage);
                });
        }

        DockerImage newImage = DockerImage.builder()
            .imageId(UUID.randomUUID().toString().replace("-", "").substring(0, 12))
            .name(imageName)
            .namespace(namespace)
            .createdAt(LocalDateTime.now())
            .tag(tag)
            .location(ImageLocation.LOCAL)
            .simulation(simulation)
            .build();

        dockerImageRepository.save(newImage);

        if (!"<none>".equals(repository)) {
            return String.format("Successfully built %s\nSuccessfully tagged %s:%s",
                newImage.getImageId(), repository, tag);
        }
        return String.format("Successfully built %s", newImage.getImageId());
    }


    @Override
    @Transactional
    public List<String> pruneImages() {
        Simulation simulation = getCurrentSimulation();
        List<DockerImage> allLocalImages = dockerImageRepository.findAllBySimulationAndLocation(
            simulation, ImageLocation.LOCAL);

        // 현재 시뮬레이션에 존재하는 모든 컨테이너 목록을 가져옵니다.
        List<DockerContainer> allContainers = simulation.getDockerContainers();
        Set<String> usedImageIds = allContainers.stream()
            .map(DockerContainer::getBaseImageId)
            .collect(Collectors.toSet());

        List<DockerImage> imagesToPrune = allLocalImages.stream()
            .filter(image -> !usedImageIds.contains(image.getImageId()) &&
                "<none>".equals(image.getName()) && "<none>".equals(image.getTag()))
            .toList();

        if (imagesToPrune.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> prunedImageIds = imagesToPrune.stream()
            .map(DockerImage::getImageId)
            .toList();

        dockerImageRepository.deleteAll(imagesToPrune);
        return prunedImageIds;
    }

    @Override
    @Transactional(readOnly = true)
    public String inspectImage(String imageNameOrId) {
        Simulation simulation = getCurrentSimulation();
        List<DockerImage> imagesToInspect = dockerImageFinder.findImages(imageNameOrId, simulation)
            .stream().filter(i -> i.getLocation() == ImageLocation.LOCAL).toList();

        if (imagesToInspect.isEmpty()) {
            return "Error: No such image: " + imageNameOrId;
        }
        if (imagesToInspect.size() > 1) {
            return "Error: \"" + imageNameOrId
                + "\" is ambiguous and matches multiple images. Please specify a tag or a full image ID.";
        }

        DockerImage image = imagesToInspect.get(0);
        return "[\n" +
            "    {\n" +
            "        \"Id\": \"sha256:" + image.getImageId() + "\",\n" +
            "        \"RepoTags\": [\n" +
            "            \"" + image.getFullNameWithTag() + "\"\n" +
            "        ],\n" +
            "        \"Created\": \"" + image.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME)
            + "Z\",\n" +
            "        \"Location\": \"" + image.getLocation().name() + "\"\n" +
            "    }\n" +
            "]";
    }

    private Simulation getCurrentSimulation() {
        String simulationId = SimulationContextHolder.getSimulationId();
        return simulationFinder.findBySimulationId(simulationId);
    }

    private String formatDuration(Duration duration) {
        long days = duration.toDays();
        if (days > 0) {
            return days + (days == 1 ? " day ago" : " days ago");
        }
        long hours = duration.toHours();
        if (hours > 0) {
            return hours + (hours == 1 ? " hour ago" : " hours ago");
        }
        long minutes = duration.toMinutes();
        if (minutes > 0) {
            return minutes + (minutes == 1 ? " minute ago" : " minutes ago");
        }
        return "just now";
    }

    private boolean isPotentialHexId(String str) {
        if (str == null || str.isEmpty() || str.contains("/") || str.contains(":")) {
            return false;
        }
        return str.matches("^[0-9a-fA-F]+$");
    }

    private String getConsoleHeader() {
        return String.format("%-25s %-20s %-15s %-20s", "REPOSITORY", "TAG", "IMAGE ID", "CREATED");
    }
}

package com.dockersim.executor;

import com.dockersim.domain.*;
import com.dockersim.parser.ParsedDockerCommand;
import com.dockersim.repository.ContainerRepository;
import com.dockersim.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * 이미지 관련 비즈니스 로직 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ImageService {

    private final ImageRepository imageRepository;
    private final ContainerRepository containerRepository;

    /**
     * docker images 명령어 실행
     */
    public CommandExecuteResult executeImages(ParsedDockerCommand command, Simulation simulation) {
        List<Image> images = imageRepository.findBySimulationId(simulation.getId());
        return createImagesResult(command, images, simulation);
    }

    /**
     * docker pull 명령어 실행
     */
    public CommandExecuteResult executePull(ParsedDockerCommand command, Simulation simulation) {
        String imageName = command.getTarget();
        if (imageName == null) {
            throw new RuntimeException("이미지 이름이 필요합니다");
        }

        boolean imageExists = imageRepository.existsBySimulationIdAndImageName(simulation.getId(), imageName);
        if (imageExists) {
            return createPullExistingResult(command, imageName, simulation);
        }

        Image image = ensureImageExists(imageName, simulation);
        return createPullResult(command, image, simulation);
    }

    /**
     * docker rmi 명령어 실행
     */
    public CommandExecuteResult executeRmi(ParsedDockerCommand command, Simulation simulation) {
        String imageName = command.getTarget();
        if (imageName == null) {
            throw new RuntimeException("이미지 이름이 필요합니다");
        }

        Image image = imageRepository.findBySimulationIdAndImageName(simulation.getId(), imageName)
                .orElse(null);
        if (image == null) {
            throw new RuntimeException("이미지를 찾을 수 없습니다: " + imageName);
        }

        List<Container> containers = containerRepository.findBySimulationIdAndImage(simulation.getId(),
                imageName);
        if (!containers.isEmpty() && !command.isFlagSet("f") && !command.isFlagSet("force")) {
            throw new RuntimeException("이미지를 사용하는 컨테이너가 있습니다. 먼저 컨테이너를 제거하거나 -f 옵션을 사용하세요: " + imageName);
        }

        imageRepository.delete(image);
        return createRmiResult(command, image, simulation);
    }

    /**
     * 이미지 존재 확인 및 생성
     */
    public Image ensureImageExists(String imageName, Simulation simulation) {
        return imageRepository.findBySimulationIdAndImageName(simulation.getId(), imageName)
                .orElseGet(() -> {
                    String[] parts = imageName.split(":");
                    String repository = parts[0];
                    String tag = parts.length > 1 ? parts[1] : "latest";

                    Image newImage = Image.builder()
                            .imageId(generateImageId())
                            .repository(repository)
                            .tag(tag)
                            .simulation(simulation)
                            .build();

                    return imageRepository.save(newImage);
                });
    }

    /**
     * 이미지 존재 여부 확인
     */
    public boolean imageExists(String imageName, Simulation simulation) {
        return imageRepository.existsBySimulationIdAndImageName(simulation.getId(), imageName);
    }

    // === Private Helper Methods ===

    private String generateImageId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }

    // === Result Creation Methods === (간소화)

    private CommandExecuteResult createImagesResult(ParsedDockerCommand command, List<Image> images,
            Simulation simulation) {
        List<String> outputLines = new java.util.ArrayList<>();
        outputLines.add("REPOSITORY          TAG       IMAGE ID       CREATED");
        for (Image image : images) {
            String line = String.format("%-20s   %-8s   %-12s   %s",
                    image.getRepository(), image.getTag(),
                    image.getShortImageId(), "2 hours ago");
            outputLines.add(line);
        }

        return CommandExecuteResult.builder()
                .command(command.getOriginalCommand())
                .success(true)
                .console(ConsoleOutput.builder().output(outputLines).success(true).build())
                .stateChanges(createEmptyStateChanges())
                .summary(createSummary(simulation))
                .hints(createImagesHints())
                .executedAt(LocalDateTime.now())
                .build();
    }

    private CommandExecuteResult createPullExistingResult(ParsedDockerCommand command, String imageName,
            Simulation simulation) {
        List<String> outputLines = Arrays.asList(
                "Using default tag: latest",
                "latest: Pulling from library/" + imageName.split(":")[0],
                "Status: Image is up to date for " + imageName);

        return CommandExecuteResult.builder()
                .command(command.getOriginalCommand())
                .success(true)
                .console(ConsoleOutput.builder().output(outputLines).success(true).build())
                .stateChanges(createEmptyStateChanges())
                .summary(createSummary(simulation))
                .hints(createPullExistingHints(imageName))
                .executedAt(LocalDateTime.now())
                .build();
    }

    private CommandExecuteResult createPullResult(ParsedDockerCommand command, Image image,
            Simulation simulation) {
        List<String> outputLines = Arrays.asList(
                "Using default tag: latest",
                "latest: Pulling from library/" + image.getRepository(),
                "31b3f1ad4ce1: Pull complete",
                "fd42b079d0f8: Pull complete",
                "Status: Downloaded newer image for " + image.getFullName());

        return CommandExecuteResult.builder()
                .command(command.getOriginalCommand())
                .success(true)
                .console(ConsoleOutput.builder().output(outputLines).success(true).build())
                .stateChanges(StateChanges.builder()
                        .images(ResourceChanges.builder()
                                .added(Arrays.asList(image))
                                .modified(Collections.emptyList())
                                .removed(Collections.emptyList())
                                .build())
                        .containers(createEmptyResourceChanges())
                        .networks(createEmptyResourceChanges())
                        .volumes(createEmptyResourceChanges())
                        .build())
                .summary(createSummary(simulation))
                .hints(createPullHints(image))
                .executedAt(LocalDateTime.now())
                .resourceId(image.getImageId())
                .build();
    }

    private CommandExecuteResult createRmiResult(ParsedDockerCommand command, Image image,
            Simulation simulation) {
        List<String> outputLines = Arrays.asList("Untagged: " + image.getFullName(),
                "Deleted: sha256:" + image.getImageId());

        return CommandExecuteResult.builder()
                .command(command.getOriginalCommand())
                .success(true)
                .console(ConsoleOutput.builder().output(outputLines).success(true).build())
                .stateChanges(StateChanges.builder()
                        .images(ResourceChanges.builder()
                                .removed(Arrays.asList(image))
                                .added(Collections.emptyList())
                                .modified(Collections.emptyList())
                                .build())
                        .containers(createEmptyResourceChanges())
                        .networks(createEmptyResourceChanges())
                        .volumes(createEmptyResourceChanges())
                        .build())
                .summary(createSummary(simulation))
                .hints(createRmiHints(image))
                .executedAt(LocalDateTime.now())
                .resourceId(image.getImageId())
                .build();
    }

    // === Helper Methods ===

    private StateSummary createSummary(Simulation simulation) {
        return StateSummary.builder()
                .totalContainers(containerRepository.countBySimulationId(simulation.getId()))
                .runningContainers(containerRepository.countRunningContainersBySimulationId(simulation.getId()))
                .totalImages(imageRepository.countBySimulationId(simulation.getId()))
                .totalNetworks(0L)
                .totalVolumes(0L)
                .build();
    }

    private StateChanges createEmptyStateChanges() {
        return StateChanges.builder()
                .containers(createEmptyResourceChanges())
                .images(createEmptyResourceChanges())
                .networks(createEmptyResourceChanges())
                .volumes(createEmptyResourceChanges())
                .build();
    }

    private ResourceChanges createEmptyResourceChanges() {
        return ResourceChanges.builder()
                .added(Collections.emptyList())
                .modified(Collections.emptyList())
                .removed(Collections.emptyList())
                .build();
    }

    // === Hint Creation Methods ===

    private LearningHints createImagesHints() {
        return LearningHints.builder()
                .message("이미지 목록을 조회했습니다")
                .nextSuggestions(Arrays.asList("docker run <이미지명>", "docker pull <이미지명>"))
                .learningTip("💡 이미지 삭제: docker rmi <이미지명>")
                .build();
    }

    private LearningHints createPullExistingHints(String imageName) {
        return LearningHints.builder()
                .message("이미지가 최신 상태입니다")
                .nextSuggestions(Arrays.asList("docker run " + imageName, "docker images"))
                .learningTip("💡 컨테이너 실행: docker run " + imageName)
                .build();
    }

    private LearningHints createPullHints(Image image) {
        return LearningHints.builder()
                .message("이미지가 다운로드되었습니다")
                .nextSuggestions(Arrays.asList("docker run " + image.getFullName(), "docker images"))
                .learningTip("💡 컨테이너 실행: docker run " + image.getFullName())
                .build();
    }

    private LearningHints createRmiHints(Image image) {
        return LearningHints.builder()
                .message("이미지가 제거되었습니다")
                .nextSuggestions(Arrays.asList("docker images", "docker pull " + image.getFullName()))
                .learningTip("💡 이미지 제거는 되돌릴 수 없습니다")
                .build();
    }
}